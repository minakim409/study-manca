package com.study.manca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.manca.dto.RentalRequest;
import com.study.manca.dto.RentalResponse;
import com.study.manca.entity.Book;
import com.study.manca.entity.Member;
import com.study.manca.entity.Rental;
import com.study.manca.service.RentalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {RentalController.class})
@DisplayName("RentalController 테스트")
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalService rentalService;

    private RentalResponse createRentalResponse(Long id, Long memberId, String memberName,
                                                 Long bookId, String bookCode, String bookTitle,
                                                 LocalDateTime rentalDateTime, LocalDateTime returnDateTime,
                                                 LocalDateTime dueDateTime, Rental.RentalStatus status, String remarks) {
        Member member = Member.builder().name(memberName).email("test@test.com").phone("010-0000-0000").build();
        Book book = Book.builder()
                .bookCode(bookCode)
                .title(bookTitle)
                .author("테스트 작가")
                .publisher("테스트 출판사")
                .volume(1)
                .genre("테스트")
                .status(status == Rental.RentalStatus.RETURNED ? Book.BookStatus.AVAILABLE : Book.BookStatus.RENTED)
                .condition(Book.BookCondition.GOOD)
                .location("A-01")
                .build();

        Rental rental = Rental.builder()
                .member(member)
                .book(book)
                .rentalDateTime(rentalDateTime)
                .returnDateTime(returnDateTime)
                .dueDateTime(dueDateTime)
                .status(status)
                .remarks(remarks)
                .build();

        try {
            java.lang.reflect.Field rentalIdField = Rental.class.getDeclaredField("id");
            rentalIdField.setAccessible(true);
            rentalIdField.set(rental, id);

            java.lang.reflect.Field memberIdField = Member.class.getDeclaredField("id");
            memberIdField.setAccessible(true);
            memberIdField.set(member, memberId);

            java.lang.reflect.Field bookIdField = Book.class.getDeclaredField("id");
            bookIdField.setAccessible(true);
            bookIdField.set(book, bookId);

            java.lang.reflect.Field createdAtField = Rental.class.getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(rental, LocalDateTime.now());

            java.lang.reflect.Field updatedAtField = Rental.class.getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(rental, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return RentalResponse.from(rental);
    }

    @Test
    @DisplayName("GET /api/rentals - 전체 대여 조회 성공")
    void getAllRentals_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<RentalResponse> rentals = Arrays.asList(
                createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                        now, null, now.plusDays(7), Rental.RentalStatus.ACTIVE, null),
                createRentalResponse(2L, 2L, "김철수", 2L, "MH-001-002", "원피스",
                        now.minusDays(3), now.minusDays(1), now, Rental.RentalStatus.RETURNED, null)
        );
        given(rentalService.findAll()).willReturn(rentals);

        // when & then
        mockMvc.perform(get("/api/rentals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberName").value("홍길동"))
                .andExpect(jsonPath("$[1].memberName").value("김철수"));
    }

    @Test
    @DisplayName("GET /api/rentals/{id} - 대여 상세 조회 성공")
    void getRentalById_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        RentalResponse rental = createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                now, null, now.plusDays(7), Rental.RentalStatus.ACTIVE, "주의하여 반납");
        given(rentalService.findById(1L)).willReturn(rental);

        // when & then
        mockMvc.perform(get("/api/rentals/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.bookTitle").value("원피스"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/rentals/member/{memberId} - 회원별 대여 조회 성공")
    void getRentalsByMemberId_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<RentalResponse> rentals = Arrays.asList(
                createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                        now, null, now.plusDays(7), Rental.RentalStatus.ACTIVE, null),
                createRentalResponse(3L, 1L, "홍길동", 3L, "MH-002-001", "나루토",
                        now.minusDays(5), now.minusDays(2), now.plusDays(2), Rental.RentalStatus.RETURNED, null)
        );
        given(rentalService.findByMemberId(1L)).willReturn(rentals);

        // when & then
        mockMvc.perform(get("/api/rentals/member/{memberId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[1].memberId").value(1));
    }

    @Test
    @DisplayName("GET /api/rentals/member/{memberId}/active - 회원별 대여 중인 목록 조회 성공")
    void getActiveRentalsByMemberId_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<RentalResponse> rentals = Arrays.asList(
                createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                        now, null, now.plusDays(7), Rental.RentalStatus.ACTIVE, null)
        );
        given(rentalService.findActiveByMemberId(1L)).willReturn(rentals);

        // when & then
        mockMvc.perform(get("/api/rentals/member/{memberId}/active", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/rentals/status/{status} - 상태별 대여 조회 성공")
    void getRentalsByStatus_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<RentalResponse> rentals = Arrays.asList(
                createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                        now, null, now.plusDays(7), Rental.RentalStatus.ACTIVE, null),
                createRentalResponse(4L, 3L, "이영희", 4L, "MH-003-001", "블리치",
                        now.minusDays(2), null, now.plusDays(5), Rental.RentalStatus.ACTIVE, null)
        );
        given(rentalService.findByStatus(Rental.RentalStatus.ACTIVE)).willReturn(rentals);

        // when & then
        mockMvc.perform(get("/api/rentals/status/{status}", "ACTIVE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/rentals - 도서 대여 성공")
    void createRental_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        RentalRequest request = new RentalRequest(1L, 1L, 7, "주의하여 반납");
        RentalResponse response = createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                now, null, now.plusDays(7), Rental.RentalStatus.ACTIVE, "주의하여 반납");
        given(rentalService.create(any(RentalRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.memberName").value("홍길동"))
                .andExpect(jsonPath("$.bookTitle").value("원피스"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/rentals/{id}/return - 도서 반납 성공")
    void returnBook_Success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();
        RentalResponse response = createRentalResponse(1L, 1L, "홍길동", 1L, "MH-001-001", "원피스",
                now.minusDays(5), now, now.plusDays(2), Rental.RentalStatus.RETURNED, null);
        given(rentalService.returnBook(1L)).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/rentals/{id}/return", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.returnDateTime").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/rentals/{id}/delete - 대여 삭제 성공")
    void deleteRental_Success() throws Exception {
        // given
        doNothing().when(rentalService).delete(1L);

        // when & then
        mockMvc.perform(post("/api/rentals/{id}/delete", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
