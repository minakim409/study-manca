package com.study.manca.dto;

import com.study.manca.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "도서 등록/수정 요청")
@Getter
@NoArgsConstructor
public class BookRequest {

    @Schema(description = "도서 코드", example = "MH-001-001")
    private String bookCode;

    @Schema(description = "제목", example = "원피스")
    private String title;

    @Schema(description = "작가", example = "오다 에이이치로")
    private String author;

    @Schema(description = "출판사", example = "서울문화사")
    private String publisher;

    @Schema(description = "권수", example = "1")
    private Integer volume;

    @Schema(description = "장르", example = "액션")
    private String genre;

    @Schema(description = "대여 상태", example = "AVAILABLE")
    private Book.BookStatus status;

    @Schema(description = "책 상태", example = "GOOD")
    private Book.BookCondition condition;

    @Schema(description = "서가 위치", example = "A-01")
    private String location;

    @Schema(description = "비고")
    private String remarks;

    public BookRequest(String bookCode, String title, String author, String publisher, Integer volume,
                       String genre, Book.BookStatus status, Book.BookCondition condition,
                       String location, String remarks) {
        this.bookCode = bookCode;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.volume = volume;
        this.genre = genre;
        this.status = status;
        this.condition = condition;
        this.location = location;
        this.remarks = remarks;
    }

    public Book toEntity() {
        return Book.builder()
                .bookCode(bookCode)
                .title(title)
                .author(author)
                .publisher(publisher)
                .volume(volume)
                .genre(genre)
                .status(status != null ? status : Book.BookStatus.AVAILABLE)
                .condition(condition != null ? condition : Book.BookCondition.GOOD)
                .location(location)
                .remarks(remarks)
                .build();
    }
}
