package com.study.manca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 엔티티
 * 음료/간식 주문 관리
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 주문고객

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;  // 좌석

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;  // 메뉴

    @Column(nullable = false)
    private Integer quantity;  // 수량

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;  // 총액

    @Column(nullable = false)
    private LocalDateTime orderDateTime;  // 주문일시

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;  // 주문상태

    @Column(length = 1000)
    private String remarks;  // 비고

    public enum OrderStatus {
        PENDING,    // 대기중
        PREPARING,  // 준비중
        COMPLETED,  // 완료
        CANCELLED   // 취소
    }

    /**
     * 총액 자동 계산
     */
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (this.menu != null && this.quantity != null) {
            this.totalPrice = this.menu.getPrice().multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}
