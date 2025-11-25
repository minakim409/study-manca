package com.study.manca.controller;

import com.study.manca.dto.MenuRequest;
import com.study.manca.dto.MenuResponse;
import com.study.manca.entity.Menu;
import com.study.manca.service.MenuService;
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

@Tag(name = "Menu", description = "메뉴 관리 API")
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "전체 메뉴 조회", description = "등록된 모든 메뉴를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenus() {
        List<MenuResponse> menus = menuService.findAll();
        return ResponseEntity.ok(menus);
    }

    @Operation(summary = "판매 가능 메뉴 조회", description = "현재 판매 가능한 메뉴만 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/available")
    public ResponseEntity<List<MenuResponse>> getAvailableMenus() {
        List<MenuResponse> menus = menuService.findAvailable();
        return ResponseEntity.ok(menus);
    }

    @Operation(summary = "카테고리별 메뉴 조회", description = "특정 카테고리의 판매 가능 메뉴를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuResponse>> getMenusByCategory(
            @Parameter(description = "메뉴 카테고리", required = true) @PathVariable Menu.MenuCategory category) {
        List<MenuResponse> menus = menuService.findByCategory(category);
        return ResponseEntity.ok(menus);
    }

    @Operation(summary = "메뉴 상세 조회", description = "ID로 특정 메뉴의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MenuResponse> getMenuById(
            @Parameter(description = "메뉴 ID", required = true) @PathVariable Long id) {
        MenuResponse menu = menuService.findById(id);
        return ResponseEntity.ok(menu);
    }

    @Operation(summary = "메뉴 등록", description = "새로운 메뉴를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "메뉴 등록 정보")
            @RequestBody MenuRequest request) {
        MenuResponse createdMenu = menuService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenu);
    }

    @Operation(summary = "메뉴 정보 전체 수정", description = "메뉴의 모든 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @PostMapping("/{id}/update")
    public ResponseEntity<MenuResponse> updateMenu(
            @Parameter(description = "메뉴 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 메뉴 정보")
            @RequestBody MenuRequest request) {
        MenuResponse updatedMenu = menuService.update(id, request);
        return ResponseEntity.ok(updatedMenu);
    }

    @Operation(summary = "메뉴 정보 부분 수정", description = "메뉴의 일부 정보만 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @PostMapping("/{id}/update-partial")
    public ResponseEntity<MenuResponse> updateMenuPartial(
            @Parameter(description = "메뉴 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 메뉴 정보 (일부)")
            @RequestBody MenuRequest request) {
        MenuResponse updatedMenu = menuService.updatePartial(id, request);
        return ResponseEntity.ok(updatedMenu);
    }

    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "메뉴를 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteMenu(
            @Parameter(description = "메뉴 ID", required = true) @PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.ok().build();
    }
}
