package com.study.manca.service;

import com.study.manca.dto.RentalRequest;
import com.study.manca.dto.RentalResponse;
import com.study.manca.entity.Book;
import com.study.manca.entity.Member;
import com.study.manca.entity.Rental;
import com.study.manca.repository.BookRepository;
import com.study.manca.repository.MemberRepository;
import com.study.manca.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    private static final int DEFAULT_RENTAL_DAYS = 7;
    private static final int MAX_RENTAL_COUNT = 3;

    public List<RentalResponse> findAll() {
        return rentalRepository.findAll().stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    public RentalResponse findById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + id));
        return RentalResponse.from(rental);
    }

    public List<RentalResponse> findByMemberId(Long memberId) {
        return rentalRepository.findByMemberId(memberId).stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    public List<RentalResponse> findByStatus(Rental.RentalStatus status) {
        return rentalRepository.findByStatus(status).stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    public List<RentalResponse> findActiveByMemberId(Long memberId) {
        return rentalRepository.findByMemberIdAndStatus(memberId, Rental.RentalStatus.ACTIVE).stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse create(RentalRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + request.getMemberId()));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + request.getBookId()));

        // 대여 가능 여부 확인
        if (book.getStatus() != Book.BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available for rental: " + book.getTitle());
        }

        // 최대 대여 권수 확인
        int activeRentalCount = rentalRepository.countByMemberIdAndStatus(member.getId(), Rental.RentalStatus.ACTIVE);
        if (activeRentalCount >= MAX_RENTAL_COUNT) {
            throw new IllegalStateException("Maximum rental limit exceeded. Current: " + activeRentalCount + ", Max: " + MAX_RENTAL_COUNT);
        }

        // 연체 중인 대여 확인
        List<Rental> overdueRentals = rentalRepository.findByMemberIdAndStatus(member.getId(), Rental.RentalStatus.OVERDUE);
        if (!overdueRentals.isEmpty()) {
            throw new IllegalStateException("Member has overdue rentals. Please return them first.");
        }

        int rentalDays = request.getRentalDays() != null ? request.getRentalDays() : DEFAULT_RENTAL_DAYS;

        Rental rental = Rental.builder()
                .member(member)
                .book(book)
                .rentalDateTime(LocalDateTime.now())
                .dueDateTime(LocalDateTime.now().plusDays(rentalDays))
                .remarks(request.getRemarks())
                .build();

        Rental savedRental = rentalRepository.save(rental);
        return RentalResponse.from(savedRental);
    }

    @Transactional
    public RentalResponse returnBook(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + id));

        if (rental.getStatus() == Rental.RentalStatus.RETURNED) {
            throw new IllegalStateException("Book has already been returned.");
        }

        rental.returnBook();
        return RentalResponse.from(rental);
    }

    @Transactional
    public void delete(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new IllegalArgumentException("Rental not found with id: " + id);
        }
        rentalRepository.deleteById(id);
    }
}
