package com.study.manca.dto;

import com.study.manca.entity.Rental;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "대여 응답")
@Getter
public class RentalResponse {

    @Schema(description = "대여 ID", example = "1")
    private final Long id;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "회원 이름", example = "홍길동")
    private final String memberName;

    @Schema(description = "도서 ID", example = "1")
    private final Long bookId;

    @Schema(description = "도서 코드", example = "MH-001-001")
    private final String bookCode;

    @Schema(description = "도서 제목", example = "원피스")
    private final String bookTitle;

    @Schema(description = "대여일시")
    private final LocalDateTime rentalDateTime;

    @Schema(description = "반납일시")
    private final LocalDateTime returnDateTime;

    @Schema(description = "반납예정일시")
    private final LocalDateTime dueDateTime;

    @Schema(description = "대여 상태", example = "ACTIVE")
    private final Rental.RentalStatus status;

    @Schema(description = "연체 여부", example = "false")
    private final Boolean isOverdue;

    @Schema(description = "비고")
    private final String remarks;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public RentalResponse(Rental rental) {
        this.id = rental.getId();
        this.memberId = rental.getMember().getId();
        this.memberName = rental.getMember().getName();
        this.bookId = rental.getBook().getId();
        this.bookCode = rental.getBook().getBookCode();
        this.bookTitle = rental.getBook().getTitle();
        this.rentalDateTime = rental.getRentalDateTime();
        this.returnDateTime = rental.getReturnDateTime();
        this.dueDateTime = rental.getDueDateTime();
        this.status = rental.getStatus();
        this.isOverdue = rental.isOverdue();
        this.remarks = rental.getRemarks();
        this.createdAt = rental.getCreatedAt();
        this.updatedAt = rental.getUpdatedAt();
    }

    public static RentalResponse from(Rental rental) {
        return new RentalResponse(rental);
    }
}
