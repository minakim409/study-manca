package com.study.manca.repository;

import com.study.manca.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByMemberId(Long memberId);

    List<Rental> findByBookId(Long bookId);

    List<Rental> findByStatus(Rental.RentalStatus status);

    List<Rental> findByMemberIdAndStatus(Long memberId, Rental.RentalStatus status);

    int countByMemberIdAndStatus(Long memberId, Rental.RentalStatus status);
}
