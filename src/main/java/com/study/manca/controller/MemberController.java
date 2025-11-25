package com.study.manca.controller;

import com.study.manca.dto.MemberResponse;
import com.study.manca.dto.MemberRequest;
import com.study.manca.service.MemberService;
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

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "전체 회원 조회", description = "등록된 모든 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllUsers() {
        List<MemberResponse> users = memberService.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "회원 상세 조회", description = "ID로 특정 회원의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getUserById(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long id) {
        MemberResponse user = memberService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "회원 등록", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    @PostMapping
    public ResponseEntity<MemberResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 등록 정보")
            @RequestBody MemberRequest request) {
        MemberResponse createdUser = memberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "회원 정보 전체 수정", description = "회원의 모든 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PostMapping("/{id}/update")
    public ResponseEntity<MemberResponse> updateUser(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 회원 정보")
            @RequestBody MemberRequest request) {
        MemberResponse updatedUser = memberService.update(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "회원 정보 부분 수정", description = "회원의 일부 정보만 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PostMapping("/{id}/update-partial")
    public ResponseEntity<MemberResponse> updateUserPartial(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 회원 정보 (일부)")
            @RequestBody MemberRequest request) {
        MemberResponse updatedUser = memberService.updatePartial(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.ok().build();
    }
}
