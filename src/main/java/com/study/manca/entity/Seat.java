package com.study.manca.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 좌석 엔티티
 * 만화카페 좌석 관리
 */
@Entity
@Table(name = "seats")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String seatNumber;  // 좌석번호 (예: A-01, B-05)

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeatType type;  // 좌석타입

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;  // 좌석상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_member_id")
    private Member currentMember;  // 현재 사용중인 고객

    @Column(length = 1000)
    private String remarks;  // 비고

    public enum SeatType {
        REGULAR,    // 일반석
        PREMIUM,    // 프리미엄석 (리클라이너)
        COUPLE,     // 커플석
        ROOM        // 룸
    }

    public enum SeatStatus {
        AVAILABLE,      // 사용가능
        OCCUPIED,       // 사용중
        MAINTENANCE     // 정비중
    }

    /**
     * 좌석 배정
     */
    public void assignToMember(Member member) {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("사용 가능한 좌석이 아닙니다.");
        }
        this.currentMember = member;
        this.status = SeatStatus.OCCUPIED;
    }

    /**
     * 좌석 해제
     */
    public void release() {
        this.currentMember = null;
        this.status = SeatStatus.AVAILABLE;
    }
}
