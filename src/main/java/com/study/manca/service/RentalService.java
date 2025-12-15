package com.study.manca.service;

import com.study.manca.dto.*;
import com.study.manca.entity.Book;
import com.study.manca.entity.Member;
import com.study.manca.entity.Rental;
import com.study.manca.repository.BookRepository;
import com.study.manca.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberService memberService;
    private final BookService bookService;

    public List<RentalResponse> rental(RentalRequest rentalRequest) {
        Member member = memberService.findByEmail(rentalRequest.getEmail());
        // bookId 로 bookstatusCheck -> If it's available, 대여. Not available, 대여불가
        // 3개 -> 실제 가능한 건 2개 -> order 전부 drop
        boolean availableCheck = bookService.checkAllAvailable(rentalRequest.getBookIds());
        if (!availableCheck) {
            throw new IllegalArgumentException("Some of books are NOT available");
        }
        List<Book> books = bookService.updateBooksStatus(rentalRequest.getBookIds());
// TODO: rental, return 만들기
//        1. req/res 에 entity 를 담지 말자.
//        2. 기능을 정의할때는 in/out 을 먼저 설정한다.
//        3. 로직을 작성할때는 data를 먼저 다룬다.

    }

    // 전체 사용자 조회 (GET)
//    public List<BookResponse> findAll() {
//        return rentalRepository.findAll().stream()
//                .map(BookResponse::from)
//                .collect(Collectors.toList());
//    }

    // 특정 사용자 조회 (GET)
    public BookResponse findById(Long id) {
        Book book = rentalRepository.findById(id)
                                    .orElseThrow(()
                                    -> new IllegalArgumentException("Book not found with id: " + id));

        return BookResponse.from(book);
    }

    // 사용자 생성 (POST)
    @Transactional
    public void create(BookRequest request) {
        // 이메일 중복 확인
        if (rentalRepository.existsByBookCode(request.getBookCode())) {
            throw new IllegalArgumentException("BookCode already exists: " + request.getBookCode());
        }

        Book book = request.toEntity();
        rentalRepository.save(book);
    }

    //Update 목적: 기본 정보 update 이 아닌 상태를 update 하기 위한 method 다
    @Transactional
    public BookResponse updateBookStatus(Long id, BookUpdateRequest request) {
        Book book = rentalRepository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        // -> 논리키로 검증하나 더 추가 -> 데이터의 정합성(Consistency) 과 무결성(Integrity) 을 보장하기 위해서야.
        if (!book.getBookCode().equals(request.getBookCode())) {
            throw new IllegalArgumentException("BookCode does not match request code: " + request.getBookCode());
        }
        book.setVolume(request.getVolume());
        book.setStatus(request.getStatus());
        book.setCondition(request.getCondition());
        book.setLocation(request.getLocation());
        book.setRemarks(request.getRemarks());

        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
        rentalRepository.deleteById(id);
    }
}
