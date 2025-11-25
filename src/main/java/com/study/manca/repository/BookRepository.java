package com.study.manca.repository;

import com.study.manca.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByBookCode(String bookCode);

    List<Book> findByStatus(Book.BookStatus status);

    List<Book> findByGenre(String genre);

    List<Book> findByTitle(String title);

    List<Book> findByTitleContaining(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByAuthorContaining(String author);

    boolean existsByBookCode(String bookCode);
}
