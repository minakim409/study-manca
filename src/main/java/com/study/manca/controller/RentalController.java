package com.study.manca.controller;

import com.study.manca.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rental", description = "Rental API")
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;



    // 설계
//    @Operation(summary = "전체 책 조회")
//    @ApiResponse(responseCode = "200", description = "조회 성공")
//    @GetMapping
//    public ResponseEntity<List<BookResponse>> getAllBooks() {
//        List<BookResponse> books = rentalService.findAll();
//        return ResponseEntity.ok(books);
//    }

    // 실물 만화 카페???? barcode(bookCode) - scan -> 조회 -> 상태 확인
    // 조회 -> available -> 대여 가능 -> method 를 호출 -> NOT AVAILABLE
    @Operation(summary = "대여", description = "BookCode 로 대여.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<RentalResponse> rentalBook(
            @RequestBody RentalRequest request) {
        // request 에는 email, bookId, remarks 만 있기 때문에 Rental Entity 에 있는 각종 일시들을 반환해야된다.
        // data row 를 가공했기때문에, 최종적으로 결과물을 전달해야한다.
        BookResponse book = rentalService.rental(request);
        return ResponseEntity.ok(book);
    }

    @Operation(summary = "회원 등록", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
    })
    @PostMapping
    public ResponseEntity<Void> createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "책 등록 정보")
            @RequestBody BookRequest request) {
        rentalService.create(request);
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

        BookResponse updatedBook = rentalService.updateBookStatus(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    //TODO: DELETE method
    @Operation(summary = "도서 삭제", description = "도서 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long id) {
        rentalService.delete(id);
        return ResponseEntity.ok().build();
    }
}
