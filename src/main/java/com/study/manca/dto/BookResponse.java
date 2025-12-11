package com.study.manca.dto;

import com.study.manca.entity.Book;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponse {

    private Long id;
    private String bookCode;  // 도서코드 (예: MH-001-001)
    private String title;  // 제목
    private String author;  // 작가
    private String publisher;  // 출판사
    private int volume;  // 권수, wrapper class
    private String genre;  // 장르 (예: 액션, 로맨스, SF, 판타지)
    private String status;  // 대여상태
    private String condition;  // 책 상태
    private String location;  // 서가위치 (예: A-01, B-03)
    private String remarks;  // 비고

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .bookCode(book.getBookCode())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .volume(book.getVolume())
                .genre(book.getGenre())
                .status(book.getStatus().name())
                .condition(book.getCondition().name())
                .location(book.getLocation())
                .remarks(book.getRemarks())
                .build();
    }
}