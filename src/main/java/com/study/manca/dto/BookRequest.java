package com.study.manca.dto;

import com.study.manca.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "회원 등록/수정 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    private String bookCode;  // 도서코드 (예: MH-001-001)
    private String title;  // 제목
    private String author;  // 작가
    private String publisher;  // 출판사
    private Integer volume;  // 권수, wrapper class 는 null 될 수 있음
    private String genre;  // 장르 (예: 액션, 로맨스, SF, 판타지)
    private Book.BookStatus status;  // 대여상태
    private Book.BookCondition condition;  // 책 상태
    private String location;  // 서가위치 (예: A-01, B-03)
    private String remarks;  // 비고

    public Book toEntity() {
        return Book.builder()
                .bookCode(bookCode)
                .title(title)
                .author(author)
                .publisher(publisher)
                .volume(volume)
                .genre(genre)
                .status(status)
                .condition(condition)
                .location(location)
                .remarks(remarks)
                .build();
    }
}
