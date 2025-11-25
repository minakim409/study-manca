package com.study.manca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "대여 등록 요청")
@Getter
@NoArgsConstructor
public class RentalRequest {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "대여 기간(일)", example = "7")
    private Integer rentalDays;

    @Schema(description = "비고")
    private String remarks;

    public RentalRequest(Long memberId, Long bookId, Integer rentalDays, String remarks) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.rentalDays = rentalDays;
        this.remarks = remarks;
    }
}
