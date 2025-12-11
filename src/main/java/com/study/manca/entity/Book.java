package com.study.manca.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 만화책 엔티티
 * 만화카페의 만화책 정보 관리
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String bookCode;  // 도서코드 (예: MH-001-001)

    @Column(nullable = false, length = 200)
    private String title;  // 제목

    @Column(nullable = false, length = 100)
    private String author;  // 작가

    @Column(nullable = false, length = 100)
    private String publisher;  // 출판사

    @Column(nullable = false)
    private Integer volume;  // 권수, wrapper class 는 null 될 수 있음

    @Column(nullable = false, length = 50)
    private String genre;  // 장르 (예: 액션, 로맨스, SF, 판타지)

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookStatus status = BookStatus.AVAILABLE;  // 대여상태

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookCondition condition = BookCondition.GOOD;  // 책 상태

    @Column(length = 50)
    private String location;  // 서가위치 (예: A-01, B-03)

    @Column(length = 1000)
    private String remarks;  // 비고

    public enum BookStatus {
        AVAILABLE,  // 대여가능
        RENTED,     // 대여중
        LOST,       // 분실
        DAMAGED     // 훼손
    }

    public enum BookCondition {
        EXCELLENT,  // 최상
        GOOD,       // 양호
        FAIR,       // 보통
        POOR        // 불량
    }
}
