package com.study.manca.dto;

import com.study.manca.entity.Seat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "좌석 응답")
@Getter
public class SeatResponse {

    @Schema(description = "좌석 ID", example = "1")
    private final Long id;

    @Schema(description = "좌석 번호", example = "A-01")
    private final String seatNumber;

    @Schema(description = "좌석 타입", example = "REGULAR")
    private final Seat.SeatType type;

    @Schema(description = "좌석 상태", example = "AVAILABLE")
    private final Seat.SeatStatus status;

    @Schema(description = "현재 사용 회원 ID")
    private final Long currentMemberId;

    @Schema(description = "현재 사용 회원 이름")
    private final String currentMemberName;

    @Schema(description = "비고", example = "창가 자리")
    private final String remarks;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public SeatResponse(Seat seat) {
        this.id = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.type = seat.getType();
        this.status = seat.getStatus();
        this.currentMemberId = seat.getCurrentMember() != null ? seat.getCurrentMember().getId() : null;
        this.currentMemberName = seat.getCurrentMember() != null ? seat.getCurrentMember().getName() : null;
        this.remarks = seat.getRemarks();
        this.createdAt = seat.getCreatedAt();
        this.updatedAt = seat.getUpdatedAt();
    }

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(seat);
    }
}
