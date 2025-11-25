package com.study.manca.controller;

import com.study.manca.dto.SeatRequest;
import com.study.manca.dto.SeatResponse;
import com.study.manca.entity.Seat;
import com.study.manca.service.SeatService;
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

@Tag(name = "Seat", description = "좌석 관리 API")
@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @Operation(summary = "전체 좌석 조회", description = "모든 좌석 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<SeatResponse>> getAllSeats() {
        List<SeatResponse> seats = seatService.findAll();
        return ResponseEntity.ok(seats);
    }

    @Operation(summary = "좌석 상세 조회", description = "ID로 특정 좌석의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> getSeatById(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long id) {
        SeatResponse seat = seatService.findById(id);
        return ResponseEntity.ok(seat);
    }

    @Operation(summary = "사용 가능 좌석 조회", description = "현재 사용 가능한 좌석 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/available")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats() {
        List<SeatResponse> seats = seatService.findAvailable();
        return ResponseEntity.ok(seats);
    }

    @Operation(summary = "타입별 좌석 조회", description = "특정 타입의 좌석 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SeatResponse>> getSeatsByType(
            @Parameter(description = "좌석 타입", required = true) @PathVariable Seat.SeatType type) {
        List<SeatResponse> seats = seatService.findByType(type);
        return ResponseEntity.ok(seats);
    }

    @Operation(summary = "좌석 등록", description = "새로운 좌석을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<SeatResponse> createSeat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "좌석 등록 정보")
            @RequestBody SeatRequest request) {
        SeatResponse createdSeat = seatService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSeat);
    }

    @Operation(summary = "좌석 정보 수정", description = "좌석의 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음")
    })
    @PostMapping("/{id}/update")
    public ResponseEntity<SeatResponse> updateSeat(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 좌석 정보")
            @RequestBody SeatRequest request) {
        SeatResponse updatedSeat = seatService.update(id, request);
        return ResponseEntity.ok(updatedSeat);
    }

    @Operation(summary = "좌석 배정", description = "회원에게 좌석을 배정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배정 성공"),
        @ApiResponse(responseCode = "400", description = "좌석 사용 불가"),
        @ApiResponse(responseCode = "404", description = "좌석 또는 회원을 찾을 수 없음")
    })
    @PostMapping("/{id}/assign")
    public ResponseEntity<SeatResponse> assignSeat(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long id,
            @Parameter(description = "회원 ID", required = true) @RequestParam Long memberId) {
        SeatResponse assignedSeat = seatService.assignMember(id, memberId);
        return ResponseEntity.ok(assignedSeat);
    }

    @Operation(summary = "좌석 해제", description = "좌석 사용을 해제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "해제 성공"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음")
    })
    @PostMapping("/{id}/release")
    public ResponseEntity<SeatResponse> releaseSeat(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long id) {
        SeatResponse releasedSeat = seatService.releaseSeat(id);
        return ResponseEntity.ok(releasedSeat);
    }

    @Operation(summary = "좌석 삭제", description = "좌석을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteSeat(
            @Parameter(description = "좌석 ID", required = true) @PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.ok().build();
    }
}
