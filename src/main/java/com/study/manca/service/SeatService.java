package com.study.manca.service;

import com.study.manca.dto.SeatRequest;
import com.study.manca.dto.SeatResponse;
import com.study.manca.entity.Member;
import com.study.manca.entity.Seat;
import com.study.manca.repository.MemberRepository;
import com.study.manca.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;

    public List<SeatResponse> findAll() {
        return seatRepository.findAll().stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }

    public SeatResponse findById(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with id: " + id));
        return SeatResponse.from(seat);
    }

    public List<SeatResponse> findAvailable() {
        return seatRepository.findByStatus(Seat.SeatStatus.AVAILABLE).stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }

    public List<SeatResponse> findByType(Seat.SeatType type) {
        return seatRepository.findByType(type).stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatResponse create(SeatRequest request) {
        if (seatRepository.existsBySeatNumber(request.getSeatNumber())) {
            throw new IllegalArgumentException("Seat number already exists: " + request.getSeatNumber());
        }

        Seat seat = request.toEntity();
        Seat savedSeat = seatRepository.save(seat);
        return SeatResponse.from(savedSeat);
    }

    @Transactional
    public SeatResponse update(Long id, SeatRequest request) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with id: " + id));

        seat.setSeatNumber(request.getSeatNumber());
        seat.setType(request.getType());
        seat.setStatus(request.getStatus());
        seat.setRemarks(request.getRemarks());

        return SeatResponse.from(seat);
    }

    @Transactional
    public SeatResponse assignMember(Long seatId, Long memberId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with id: " + seatId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        seat.assignToMember(member);
        return SeatResponse.from(seat);
    }

    @Transactional
    public SeatResponse releaseSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with id: " + seatId));

        seat.release();
        return SeatResponse.from(seat);
    }

    @Transactional
    public void delete(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new IllegalArgumentException("Seat not found with id: " + id);
        }
        seatRepository.deleteById(id);
    }
}
