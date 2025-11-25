package com.study.manca.repository;

import com.study.manca.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findBySeatNumber(String seatNumber);

    List<Seat> findByStatus(Seat.SeatStatus status);

    List<Seat> findByType(Seat.SeatType type);

    List<Seat> findByTypeAndStatus(Seat.SeatType type, Seat.SeatStatus status);

    boolean existsBySeatNumber(String seatNumber);
}
