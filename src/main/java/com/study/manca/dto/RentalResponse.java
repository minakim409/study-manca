package com.study.manca.dto;

import com.study.manca.entity.Book;
import com.study.manca.entity.Member;
import com.study.manca.entity.Rental;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RentalResponse {

    private Long id;
    private Member member;  // 대여고객
    private Book book;  // 대여도서
    private LocalDateTime rentalDateTime;  // 대여일시
    private LocalDateTime returnDateTime;  // 반납일시
    private LocalDateTime dueDateTime;  // 반납예정일시
    private Rental.RentalStatus status = Rental.RentalStatus.ACTIVE;  // 대여상태
    private String remarks;  // 비고

    public static RentalResponse from(Rental rental) {
        return RentalResponse.builder()
                .id(rental.getId())
                .member(rental.getMember())
                .title(rental.getTitle())
                .author(rental.getAuthor())
                .publisher(rental.getPublisher())
                .volume(rental.getVolume())
                .genre(rental.getGenre())
                .status(rental.getStatus().name())
                .condition(rental.getCondition().name())
                .location(rental.getLocation())
                .remarks(rental.getRemarks())
                .build();
    }
}