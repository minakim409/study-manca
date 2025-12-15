package com.study.manca.repository;

import com.study.manca.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Book, Long> {

    boolean existsByBookCode(String bookCode);
}
