package com.study.manca.service;

import com.study.manca.dto.BookRequest;
import com.study.manca.dto.BookResponse;
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

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
        return BookResponse.from(book);
    }

    public List<BookResponse> findAvailable() {
        return bookRepository.findByStatus(Book.BookStatus.AVAILABLE).stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public List<BookResponse> findByGenre(String genre) {
        return bookRepository.findByGenre(genre).stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public List<BookResponse> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title).stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    public List<BookResponse> searchByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author).stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByBookCode(request.getBookCode())) {
            throw new IllegalArgumentException("Book code already exists: " + request.getBookCode());
        }

        Book book = request.toEntity();
        Book savedBook = bookRepository.save(book);
        return BookResponse.from(savedBook);
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        book.setBookCode(request.getBookCode());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setVolume(request.getVolume());
        book.setGenre(request.getGenre());
        book.setStatus(request.getStatus());
        book.setCondition(request.getCondition());
        book.setLocation(request.getLocation());
        book.setRemarks(request.getRemarks());

        return BookResponse.from(book);
    }

    @Transactional
    public BookResponse updatePartial(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        if (request.getBookCode() != null) book.setBookCode(request.getBookCode());
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
        if (request.getPublisher() != null) book.setPublisher(request.getPublisher());
        if (request.getVolume() != null) book.setVolume(request.getVolume());
        if (request.getGenre() != null) book.setGenre(request.getGenre());
        if (request.getStatus() != null) book.setStatus(request.getStatus());
        if (request.getCondition() != null) book.setCondition(request.getCondition());
        if (request.getLocation() != null) book.setLocation(request.getLocation());
        if (request.getRemarks() != null) book.setRemarks(request.getRemarks());

        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
