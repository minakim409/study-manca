package com.study.manca.controller;

import com.study.manca.dto.OrderRequest;
import com.study.manca.dto.OrderResponse;
import com.study.manca.entity.Order;
import com.study.manca.service.OrderService;
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

@Tag(name = "Order", description = "주문 관리 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "전체 주문 조회", description = "모든 주문 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 상세 조회", description = "ID로 특정 주문의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long id) {
        OrderResponse order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "회원별 주문 조회", description = "특정 회원의 주문 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByMemberId(
            @Parameter(description = "회원 ID", required = true) @PathVariable Long memberId) {
        List<OrderResponse> orders = orderService.findByMemberId(memberId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "상태별 주문 조회", description = "특정 상태의 주문 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "주문 상태", required = true) @PathVariable Order.OrderStatus status) {
        List<OrderResponse> orders = orderService.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 등록", description = "새로운 주문을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "주문 등록 정보")
            @RequestBody OrderRequest request) {
        OrderResponse createdOrder = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "주문 상태 변경", description = "주문의 상태를 변경합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "변경 성공"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @PostMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long id,
            @Parameter(description = "변경할 상태", required = true) @RequestParam Order.OrderStatus status) {
        OrderResponse updatedOrder = orderService.updateStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "주문 ID", required = true) @PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }
}
