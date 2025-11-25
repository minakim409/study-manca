package com.study.manca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "주문 등록/수정 요청")
@Getter
@NoArgsConstructor
public class OrderRequest {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "좌석 ID", example = "1")
    private Long seatId;

    @Schema(description = "메뉴 ID", example = "1")
    private Long menuId;

    @Schema(description = "수량", example = "2")
    private Integer quantity;

    @Schema(description = "비고", example = "얼음 적게")
    private String remarks;

    public OrderRequest(Long memberId, Long seatId, Long menuId, Integer quantity, String remarks) {
        this.memberId = memberId;
        this.seatId = seatId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.remarks = remarks;
    }
}
