package com.study.manca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 메뉴 엔티티
 * 만화카페 음료/스낵/식사 메뉴 관리
 */
@Entity
@Table(name = "menus")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MenuCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    public enum MenuCategory {
        BEVERAGE,   // 음료
        SNACK,      // 스낵
        MEAL        // 식사
    }
}
