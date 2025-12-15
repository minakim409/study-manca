package com.study.manca.dto;

import com.study.manca.entity.Book;
import com.study.manca.entity.Member;
import com.study.manca.entity.Rental;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "rental")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequest {
    private String email;  // 대여고객 - identifier no
    private List<Long> bookIds;  // 대여도서 -> id no exist -> 이건 id 로 다뤄도 괜찮음
    private String remarks; // 비고

    // member는 물리키 book 은 논리키 -> 위험 -> 단순 채번이기때문에 위험할 수 있음
    // 중요 정보를 물리키로 다루면 굉장히 위험하다.

//    public Rental toEntity() {
//        return Rental.builder()
//                .member(member)
//                .title(title)
//                .author(author)
//                .publisher(publisher)
//                .volume(volume)
//                .genre(genre)
//                .status(status)
//                .condition(condition)
//                .location(location)
//                .remarks(remarks)
//                .build();
//    }
}
