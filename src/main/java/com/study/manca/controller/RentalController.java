package com.study.manca.controller;

import com.study.manca.dto.RentalRequest;
import com.study.manca.dto.RentalResponse;
import com.study.manca.entity.Rental;
import com.study.manca.service.RentalService;
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

@Tag(name = "Rental", description = "대여 관리 API")
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @Operation(summary = "전체 대여 조회", description = "모든 대여 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        List<RentalResponse> rentals = rentalService.findAll();
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "대여 상세 조회", description = "ID로 특정 대여의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "대여를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> getRentalById(
            @Parameter(description = "대여 ID", required = true) @PathVariable Long id) {
        RentalResponse rental = rentalService.findById(id);
        return ResponseEntity.ok(rental);
    }

    @Operation(summary = "회원별 대여 조회", description = "특정 회원의 대여 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<RentalResponse>> getRentalsByMemberId(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long memberId) {
        List<RentalResponse> rentals = rentalService.findByMemberId(memberId);
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "회원별 대여 중인 목록 조회", description = "특정 회원의 현재 대여 중인 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<RentalResponse>> getActiveRentalsByMemberId(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long memberId) {
        List<RentalResponse> rentals = rentalService.findActiveByMemberId(memberId);
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "상태별 대여 조회", description = "특정 상태의 대여 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RentalResponse>> getRentalsByStatus(
            @Parameter(description = "대여 상태", required = true) @PathVariable Rental.RentalStatus status) {
        List<RentalResponse> rentals = rentalService.findByStatus(status);
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "도서 대여", description = "새로운 대여를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "대여 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (대여 불가 상태)")
    })
    @PostMapping
    public ResponseEntity<RentalResponse> createRental(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "대여 등록 정보")
            @RequestBody RentalRequest request) {
        RentalResponse createdRental = rentalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
    }

    @Operation(summary = "도서 반납", description = "대여한 도서를 반납합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "반납 성공"),
        @ApiResponse(responseCode = "400", description = "이미 반납된 도서"),
        @ApiResponse(responseCode = "404", description = "대여를 찾을 수 없음")
    })
    @PostMapping("/{id}/return")
    public ResponseEntity<RentalResponse> returnBook(
            @Parameter(description = "대여 ID", required = true) @PathVariable Long id) {
        RentalResponse returnedRental = rentalService.returnBook(id);
        return ResponseEntity.ok(returnedRental);
    }

    @Operation(summary = "대여 삭제", description = "대여 기록을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "대여를 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteRental(
            @Parameter(description = "대여 ID", required = true) @PathVariable Long id) {
        rentalService.delete(id);
        return ResponseEntity.ok().build();
    }
}
