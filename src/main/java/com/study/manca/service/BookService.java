package com.study.manca.service;

import com.study.manca.dto.BookRequest;
import com.study.manca.dto.BookResponse;
import com.study.manca.dto.BookUpdateRequest;
import com.study.manca.dto.MemberRequest;
import com.study.manca.entity.Book;
import com.study.manca.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    // 전체 사용자 조회 (GET)
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 사용자 조회 (GET)
    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                                    .orElseThrow(()
                                    -> new IllegalArgumentException("Book not found with id: " + id));

        return BookResponse.from(book);
    }

    // 사용자 생성 (POST)
    @Transactional
    public void create(BookRequest request) {
        // 이메일 중복 확인
        if (bookRepository.existsByBookCode(request.getBookCode())) {
            throw new IllegalArgumentException("BookCode already exists: " + request.getBookCode());
        }

        Book book = request.toEntity();
        bookRepository.save(book);
    }

    @Transactional
    public BookResponse updateBookStatus(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        //TODO: validate request.bookcode equals to book.getBookCode()
        book.setVolume(request.getVolume());
        book.setStatus(request.getStatus());
        book.setCondition(request.getCondition());
        book.setLocation(request.getLocation());
        book.setRemarks(request.getRemarks());

        return BookResponse.from(book);
    }

    //TODO: DELETE
}
