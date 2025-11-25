package com.study.manca.dto;

import com.study.manca.entity.Seat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "좌석 등록/수정 요청")
@Getter
@NoArgsConstructor
public class SeatRequest {

    @Schema(description = "좌석 번호", example = "A-01")
    private String seatNumber;

    @Schema(description = "좌석 타입", example = "REGULAR")
    private Seat.SeatType type;

    @Schema(description = "좌석 상태", example = "AVAILABLE")
    private Seat.SeatStatus status;

    @Schema(description = "비고", example = "창가 자리")
    private String remarks;

    public SeatRequest(String seatNumber, Seat.SeatType type, Seat.SeatStatus status, String remarks) {
        this.seatNumber = seatNumber;
        this.type = type;
        this.status = status;
        this.remarks = remarks;
    }

    public Seat toEntity() {
        return Seat.builder()
                .seatNumber(seatNumber)
                .type(type)
                .status(status != null ? status : Seat.SeatStatus.AVAILABLE)
                .remarks(remarks)
                .build();
    }
}
