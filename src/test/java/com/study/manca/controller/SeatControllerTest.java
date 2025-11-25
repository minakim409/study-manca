package com.study.manca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.manca.dto.SeatRequest;
import com.study.manca.dto.SeatResponse;
import com.study.manca.entity.Member;
import com.study.manca.entity.Seat;
import com.study.manca.service.SeatService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {SeatController.class})
@DisplayName("SeatController 테스트")
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeatService seatService;

    private SeatResponse createSeatResponse(Long id, String seatNumber, Seat.SeatType type,
                                            Seat.SeatStatus status, Long memberId, String memberName, String remarks) {
        Seat seat = Seat.builder()
                .seatNumber(seatNumber)
                .type(type)
                .status(status)
                .remarks(remarks)
                .build();

        try {
            java.lang.reflect.Field idField = Seat.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(seat, id);

            if (memberId != null && memberName != null) {
                Member member = Member.builder().name(memberName).email("test@test.com").phone("010-0000-0000").build();
                java.lang.reflect.Field memberIdField = Member.class.getDeclaredField("id");
                memberIdField.setAccessible(true);
                memberIdField.set(member, memberId);

                java.lang.reflect.Field currentMemberField = Seat.class.getDeclaredField("currentMember");
                currentMemberField.setAccessible(true);
                currentMemberField.set(seat, member);
            }

            java.lang.reflect.Field createdAtField = Seat.class.getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(seat, LocalDateTime.now());

            java.lang.reflect.Field updatedAtField = Seat.class.getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(seat, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return SeatResponse.from(seat);
    }

    @Test
    @DisplayName("GET /api/seats - 전체 좌석 조회 성공")
    void getAllSeats_Success() throws Exception {
        // given
        List<SeatResponse> seats = Arrays.asList(
                createSeatResponse(1L, "A-01", Seat.SeatType.REGULAR, Seat.SeatStatus.AVAILABLE, null, null, "창가 자리"),
                createSeatResponse(2L, "A-02", Seat.SeatType.PREMIUM, Seat.SeatStatus.OCCUPIED, 1L, "홍길동", "리클라이너")
        );
        given(seatService.findAll()).willReturn(seats);

        // when & then
        mockMvc.perform(get("/api/seats"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].seatNumber").value("A-01"))
                .andExpect(jsonPath("$[1].seatNumber").value("A-02"));
    }

    @Test
    @DisplayName("GET /api/seats/{id} - 좌석 상세 조회 성공")
    void getSeatById_Success() throws Exception {
        // given
        SeatResponse seat = createSeatResponse(1L, "A-01", Seat.SeatType.REGULAR,
                Seat.SeatStatus.AVAILABLE, null, null, "창가 자리");
        given(seatService.findById(1L)).willReturn(seat);

        // when & then
        mockMvc.perform(get("/api/seats/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.seatNumber").value("A-01"))
                .andExpect(jsonPath("$.type").value("REGULAR"));
    }

    @Test
    @DisplayName("GET /api/seats/available - 사용 가능 좌석 조회 성공")
    void getAvailableSeats_Success() throws Exception {
        // given
        List<SeatResponse> seats = Arrays.asList(
                createSeatResponse(1L, "A-01", Seat.SeatType.REGULAR, Seat.SeatStatus.AVAILABLE, null, null, "창가 자리"),
                createSeatResponse(3L, "B-01", Seat.SeatType.COUPLE, Seat.SeatStatus.AVAILABLE, null, null, "커플석")
        );
        given(seatService.findAvailable()).willReturn(seats);

        // when & then
        mockMvc.perform(get("/api/seats/available"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[1].status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("GET /api/seats/type/{type} - 타입별 좌석 조회 성공")
    void getSeatsByType_Success() throws Exception {
        // given
        List<SeatResponse> seats = Arrays.asList(
                createSeatResponse(2L, "P-01", Seat.SeatType.PREMIUM, Seat.SeatStatus.AVAILABLE, null, null, "리클라이너"),
                createSeatResponse(5L, "P-02", Seat.SeatType.PREMIUM, Seat.SeatStatus.OCCUPIED, 1L, "홍길동", "리클라이너")
        );
        given(seatService.findByType(Seat.SeatType.PREMIUM)).willReturn(seats);

        // when & then
        mockMvc.perform(get("/api/seats/type/{type}", "PREMIUM"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("PREMIUM"));
    }

    @Test
    @DisplayName("POST /api/seats - 좌석 등록 성공")
    void createSeat_Success() throws Exception {
        // given
        SeatRequest request = new SeatRequest("A-01", Seat.SeatType.REGULAR, Seat.SeatStatus.AVAILABLE, "창가 자리");
        SeatResponse response = createSeatResponse(1L, "A-01", Seat.SeatType.REGULAR,
                Seat.SeatStatus.AVAILABLE, null, null, "창가 자리");
        given(seatService.create(any(SeatRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.seatNumber").value("A-01"));
    }

    @Test
    @DisplayName("POST /api/seats/{id}/update - 좌석 정보 수정 성공")
    void updateSeat_Success() throws Exception {
        // given
        SeatRequest request = new SeatRequest("A-01", Seat.SeatType.PREMIUM, Seat.SeatStatus.AVAILABLE, "업그레이드");
        SeatResponse response = createSeatResponse(1L, "A-01", Seat.SeatType.PREMIUM,
                Seat.SeatStatus.AVAILABLE, null, null, "업그레이드");
        given(seatService.update(eq(1L), any(SeatRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/seats/{id}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("PREMIUM"))
                .andExpect(jsonPath("$.remarks").value("업그레이드"));
    }

    @Test
    @DisplayName("POST /api/seats/{id}/assign - 좌석 배정 성공")
    void assignSeat_Success() throws Exception {
        // given
        SeatResponse response = createSeatResponse(1L, "A-01", Seat.SeatType.REGULAR,
                Seat.SeatStatus.OCCUPIED, 1L, "홍길동", "창가 자리");
        given(seatService.assignMember(eq(1L), eq(1L))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/seats/{id}/assign", 1L)
                        .param("memberId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OCCUPIED"))
                .andExpect(jsonPath("$.currentMemberId").value(1))
                .andExpect(jsonPath("$.currentMemberName").value("홍길동"));
    }

    @Test
    @DisplayName("POST /api/seats/{id}/release - 좌석 해제 성공")
    void releaseSeat_Success() throws Exception {
        // given
        SeatResponse response = createSeatResponse(1L, "A-01", Seat.SeatType.REGULAR,
                Seat.SeatStatus.AVAILABLE, null, null, "창가 자리");
        given(seatService.releaseSeat(1L)).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/seats/{id}/release", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.currentMemberId").isEmpty());
    }

    @Test
    @DisplayName("POST /api/seats/{id}/delete - 좌석 삭제 성공")
    void deleteSeat_Success() throws Exception {
        // given
        doNothing().when(seatService).delete(1L);

        // when & then
        mockMvc.perform(post("/api/seats/{id}/delete", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
