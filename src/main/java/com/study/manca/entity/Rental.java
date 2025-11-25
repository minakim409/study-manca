package com.study.manca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 대여 엔티티
 * 만화책 대여 내역 관리
 */
@Entity
@Table(name = "rentals")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rental extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 대여고객

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;  // 대여도서

    @Column(nullable = false)
    private LocalDateTime rentalDateTime;  // 대여일시

    @Column
    private LocalDateTime returnDateTime;  // 반납일시

    @Column(nullable = false)
    private LocalDateTime dueDateTime;  // 반납예정일시

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RentalStatus status = RentalStatus.ACTIVE;  // 대여상태

    @Column(length = 1000)
    private String remarks;  // 비고

    public enum RentalStatus {
        ACTIVE,     // 대여중
        RETURNED,   // 반납완료
        OVERDUE     // 연체중
    }

    /**
     * 반납 처리
     */
    public void returnBook() {
        this.returnDateTime = LocalDateTime.now();
        this.status = RentalStatus.RETURNED;
        this.book.setStatus(Book.BookStatus.AVAILABLE);
    }

    /**
     * 연체 여부 확인
     */
    public boolean isOverdue() {
        if (status == RentalStatus.RETURNED) {
            return false;
        }
        return LocalDateTime.now().isAfter(dueDateTime);
    }

    /**
     * 대여 시작
     */
    @PrePersist
    public void onRental() {
        this.book.setStatus(Book.BookStatus.RENTED);
    }
}
