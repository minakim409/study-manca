package com.study.manca.dto;

import com.study.manca.entity.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "메뉴 응답")
@Getter
public class MenuResponse {

    @Schema(description = "메뉴 ID", example = "1")
    private final Long id;

    @Schema(description = "메뉴명", example = "아메리카노")
    private final String name;

    @Schema(description = "카테고리", example = "BEVERAGE")
    private final Menu.MenuCategory category;

    @Schema(description = "가격", example = "3000")
    private final BigDecimal price;

    @Schema(description = "설명", example = "깊고 진한 에스프레소")
    private final String description;

    @Schema(description = "판매 가능 여부", example = "true")
    private final Boolean isAvailable;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public MenuResponse(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.category = menu.getCategory();
        this.price = menu.getPrice();
        this.description = menu.getDescription();
        this.isAvailable = menu.getIsAvailable();
        this.createdAt = menu.getCreatedAt();
        this.updatedAt = menu.getUpdatedAt();
    }

    public static MenuResponse from(Menu menu) {
        return new MenuResponse(menu);
    }
}
