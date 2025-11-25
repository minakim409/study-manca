package com.study.manca.dto;

import com.study.manca.entity.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "메뉴 등록/수정 요청")
@Getter
@NoArgsConstructor
public class MenuRequest {

    @Schema(description = "메뉴명", example = "아메리카노")
    private String name;

    @Schema(description = "카테고리", example = "BEVERAGE")
    private Menu.MenuCategory category;

    @Schema(description = "가격", example = "3000")
    private BigDecimal price;

    @Schema(description = "설명", example = "깊고 진한 에스프레소")
    private String description;

    @Schema(description = "판매 가능 여부", example = "true")
    private Boolean isAvailable;

    public MenuRequest(String name, Menu.MenuCategory category, BigDecimal price, String description, Boolean isAvailable) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public Menu toEntity() {
        return Menu.builder()
                .name(name)
                .category(category)
                .price(price)
                .description(description)
                .isAvailable(isAvailable != null ? isAvailable : true)
                .build();
    }
}
