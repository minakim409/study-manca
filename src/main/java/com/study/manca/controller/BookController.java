package com.study.manca.controller;

import com.study.manca.dto.BookRequest;
import com.study.manca.dto.BookResponse;
import com.study.manca.dto.BookUpdateRequest;
import com.study.manca.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book", description = "Book API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 설계
    @Operation(summary = "전체 책 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "회원 상세 조회", description = "ID로 특정 회원의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long id) {
        BookResponse book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    //thread
    @Operation(summary = "회원 등록", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
    })
    @PostMapping
    public ResponseEntity<Void> createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "책 등록 정보")
            @RequestBody BookRequest request) {
        bookService.create(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "도서 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음")
    })
    @PostMapping("/{id}/update")
    public ResponseEntity<BookResponse> updateBookById(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest request) {

        BookResponse updatedBook = bookService.updateBookStatus(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    //TODO: DELETE method
}
