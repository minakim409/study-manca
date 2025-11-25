package com.study.manca.dto;

import com.study.manca.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "도서 응답")
@Getter
public class BookResponse {

    @Schema(description = "도서 ID", example = "1")
    private final Long id;

    @Schema(description = "도서 코드", example = "MH-001-001")
    private final String bookCode;

    @Schema(description = "제목", example = "원피스")
    private final String title;

    @Schema(description = "작가", example = "오다 에이이치로")
    private final String author;

    @Schema(description = "출판사", example = "서울문화사")
    private final String publisher;

    @Schema(description = "권수", example = "1")
    private final Integer volume;

    @Schema(description = "장르", example = "액션")
    private final String genre;

    @Schema(description = "대여 상태", example = "AVAILABLE")
    private final Book.BookStatus status;

    @Schema(description = "책 상태", example = "GOOD")
    private final Book.BookCondition condition;

    @Schema(description = "서가 위치", example = "A-01")
    private final String location;

    @Schema(description = "비고")
    private final String remarks;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.bookCode = book.getBookCode();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.volume = book.getVolume();
        this.genre = book.getGenre();
        this.status = book.getStatus();
        this.condition = book.getCondition();
        this.location = book.getLocation();
        this.remarks = book.getRemarks();
        this.createdAt = book.getCreatedAt();
        this.updatedAt = book.getUpdatedAt();
    }

    public static BookResponse from(Book book) {
        return new BookResponse(book);
    }
}
