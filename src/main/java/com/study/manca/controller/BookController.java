package com.study.manca.controller;

import com.study.manca.dto.BookRequest;
import com.study.manca.dto.BookResponse;
import com.study.manca.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book", description = "도서 관리 API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "전체 도서 조회", description = "모든 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "도서 상세 조회", description = "ID로 특정 도서의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id) {
        BookResponse book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "대여 가능 도서 조회", description = "현재 대여 가능한 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/available")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {
        List<BookResponse> books = bookService.findAvailable();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "장르별 도서 조회", description = "특정 장르의 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<BookResponse>> getBooksByGenre(
            @Parameter(description = "장르", required = true) @PathVariable String genre) {
        List<BookResponse> books = bookService.findByGenre(genre);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "제목으로 도서 검색", description = "제목에 키워드가 포함된 도서를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponse>> searchByTitle(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword) {
        List<BookResponse> books = bookService.searchByTitle(keyword);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "작가로 도서 검색", description = "작가 이름에 키워드가 포함된 도서를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponse>> searchByAuthor(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword) {
        List<BookResponse> books = bookService.searchByAuthor(keyword);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "도서 등록", description = "새로운 도서를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "도서 등록 정보")
            @RequestBody BookRequest request) {
        BookResponse createdBook = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @Operation(summary = "도서 정보 전체 수정", description = "도서의 모든 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/{id}/update")
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 도서 정보")
            @RequestBody BookRequest request) {
        BookResponse updatedBook = bookService.update(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "도서 정보 부분 수정", description = "도서의 일부 정보만 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/{id}/update-partial")
    public ResponseEntity<BookResponse> updateBookPartial(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 도서 정보 (일부)")
            @RequestBody BookRequest request) {
        BookResponse updatedBook = bookService.updatePartial(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    @Operation(summary = "도서 삭제", description = "도서를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "도서 ID", required = true) @PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok().build();
    }
}
