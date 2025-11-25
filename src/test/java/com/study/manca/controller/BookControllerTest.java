package com.study.manca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.manca.dto.BookRequest;
import com.study.manca.dto.BookResponse;
import com.study.manca.entity.Book;
import com.study.manca.service.BookService;
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
@ContextConfiguration(classes = {BookController.class})
@DisplayName("BookController 테스트")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookResponse createBookResponse(Long id, String bookCode, String title, String author,
                                            String publisher, Integer volume, String genre,
                                            Book.BookStatus status, Book.BookCondition condition,
                                            String location, String remarks) {
        Book book = Book.builder()
                .bookCode(bookCode)
                .title(title)
                .author(author)
                .publisher(publisher)
                .volume(volume)
                .genre(genre)
                .status(status)
                .condition(condition)
                .location(location)
                .remarks(remarks)
                .build();

        try {
            java.lang.reflect.Field idField = Book.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(book, id);

            java.lang.reflect.Field createdAtField = Book.class.getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(book, LocalDateTime.now());

            java.lang.reflect.Field updatedAtField = Book.class.getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(book, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return BookResponse.from(book);
    }

    @Test
    @DisplayName("GET /api/books - 전체 도서 조회 성공")
    void getAllBooks_Success() throws Exception {
        // given
        List<BookResponse> books = Arrays.asList(
                createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null),
                createBookResponse(2L, "MH-001-002", "원피스", "오다 에이이치로", "서울문화사",
                        2, "액션", Book.BookStatus.RENTED, Book.BookCondition.GOOD, "A-01", null)
        );
        given(bookService.findAll()).willReturn(books);

        // when & then
        mockMvc.perform(get("/api/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("원피스"))
                .andExpect(jsonPath("$[0].volume").value(1));
    }

    @Test
    @DisplayName("GET /api/books/{id} - 도서 상세 조회 성공")
    void getBookById_Success() throws Exception {
        // given
        BookResponse book = createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null);
        given(bookService.findById(1L)).willReturn(book);

        // when & then
        mockMvc.perform(get("/api/books/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("원피스"))
                .andExpect(jsonPath("$.author").value("오다 에이이치로"));
    }

    @Test
    @DisplayName("GET /api/books/available - 대여 가능 도서 조회 성공")
    void getAvailableBooks_Success() throws Exception {
        // given
        List<BookResponse> books = Arrays.asList(
                createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null),
                createBookResponse(3L, "MH-002-001", "나루토", "키시모토 마사시", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.EXCELLENT, "A-02", null)
        );
        given(bookService.findAvailable()).willReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/available"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[1].status").value("AVAILABLE"));
    }

    @Test
    @DisplayName("GET /api/books/genre/{genre} - 장르별 도서 조회 성공")
    void getBooksByGenre_Success() throws Exception {
        // given
        List<BookResponse> books = Arrays.asList(
                createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null),
                createBookResponse(3L, "MH-002-001", "나루토", "키시모토 마사시", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.EXCELLENT, "A-02", null)
        );
        given(bookService.findByGenre("액션")).willReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/genre/{genre}", "액션"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].genre").value("액션"));
    }

    @Test
    @DisplayName("GET /api/books/search/title - 제목으로 도서 검색 성공")
    void searchByTitle_Success() throws Exception {
        // given
        List<BookResponse> books = Arrays.asList(
                createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null)
        );
        given(bookService.searchByTitle("원피스")).willReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/search/title")
                        .param("keyword", "원피스"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("원피스"));
    }

    @Test
    @DisplayName("GET /api/books/search/author - 작가로 도서 검색 성공")
    void searchByAuthor_Success() throws Exception {
        // given
        List<BookResponse> books = Arrays.asList(
                createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                        1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null)
        );
        given(bookService.searchByAuthor("오다")).willReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/search/author")
                        .param("keyword", "오다"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("오다 에이이치로"));
    }

    @Test
    @DisplayName("POST /api/books - 도서 등록 성공")
    void createBook_Success() throws Exception {
        // given
        BookRequest request = new BookRequest("MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null);
        BookResponse response = createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                1, "액션", Book.BookStatus.AVAILABLE, Book.BookCondition.GOOD, "A-01", null);
        given(bookService.create(any(BookRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("원피스"));
    }

    @Test
    @DisplayName("POST /api/books/{id}/update - 도서 정보 전체 수정 성공")
    void updateBook_Success() throws Exception {
        // given
        BookRequest request = new BookRequest("MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                1, "액션/모험", Book.BookStatus.AVAILABLE, Book.BookCondition.EXCELLENT, "A-01", "베스트셀러");
        BookResponse response = createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                1, "액션/모험", Book.BookStatus.AVAILABLE, Book.BookCondition.EXCELLENT, "A-01", "베스트셀러");
        given(bookService.update(eq(1L), any(BookRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/books/{id}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genre").value("액션/모험"))
                .andExpect(jsonPath("$.condition").value("EXCELLENT"));
    }

    @Test
    @DisplayName("POST /api/books/{id}/update-partial - 도서 정보 부분 수정 성공")
    void updateBookPartial_Success() throws Exception {
        // given
        BookRequest request = new BookRequest(null, null, null, null, null, null,
                Book.BookStatus.RENTED, null, null, null);
        BookResponse response = createBookResponse(1L, "MH-001-001", "원피스", "오다 에이이치로", "서울문화사",
                1, "액션", Book.BookStatus.RENTED, Book.BookCondition.GOOD, "A-01", null);
        given(bookService.updatePartial(eq(1L), any(BookRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/books/{id}/update-partial", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RENTED"));
    }

    @Test
    @DisplayName("POST /api/books/{id}/delete - 도서 삭제 성공")
    void deleteBook_Success() throws Exception {
        // given
        doNothing().when(bookService).delete(1L);

        // when & then
        mockMvc.perform(post("/api/books/{id}/delete", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
