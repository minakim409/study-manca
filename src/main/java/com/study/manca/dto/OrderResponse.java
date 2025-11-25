package com.study.manca.dto;

import com.study.manca.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "주문 응답")
@Getter
public class OrderResponse {

    @Schema(description = "주문 ID", example = "1")
    private final Long id;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "회원 이름", example = "홍길동")
    private final String memberName;

    @Schema(description = "좌석 ID", example = "1")
    private final Long seatId;

    @Schema(description = "좌석 번호", example = "A-01")
    private final String seatNumber;

    @Schema(description = "메뉴 ID", example = "1")
    private final Long menuId;

    @Schema(description = "메뉴명", example = "아메리카노")
    private final String menuName;

    @Schema(description = "단가", example = "3000")
    private final BigDecimal unitPrice;

    @Schema(description = "수량", example = "2")
    private final Integer quantity;

    @Schema(description = "총액", example = "6000")
    private final BigDecimal totalPrice;

    @Schema(description = "주문일시")
    private final LocalDateTime orderDateTime;

    @Schema(description = "주문 상태", example = "PENDING")
    private final Order.OrderStatus status;

    @Schema(description = "비고", example = "얼음 적게")
    private final String remarks;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.memberId = order.getMember().getId();
        this.memberName = order.getMember().getName();
        this.seatId = order.getSeat().getId();
        this.seatNumber = order.getSeat().getSeatNumber();
        this.menuId = order.getMenu().getId();
        this.menuName = order.getMenu().getName();
        this.unitPrice = order.getMenu().getPrice();
        this.quantity = order.getQuantity();
        this.totalPrice = order.getTotalPrice();
        this.orderDateTime = order.getOrderDateTime();
        this.status = order.getStatus();
        this.remarks = order.getRemarks();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(order);
    }
}
