package com.study.manca.service;

import com.study.manca.dto.OrderRequest;
import com.study.manca.dto.OrderResponse;
import com.study.manca.entity.Member;
import com.study.manca.entity.Menu;
import com.study.manca.entity.Order;
import com.study.manca.entity.Seat;
import com.study.manca.repository.MemberRepository;
import com.study.manca.repository.MenuRepository;
import com.study.manca.repository.OrderRepository;
import com.study.manca.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final MenuRepository menuRepository;

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> findByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + request.getMemberId()));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found with id: " + request.getSeatId()));

        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with id: " + request.getMenuId()));

        if (!menu.getIsAvailable()) {
            throw new IllegalStateException("Menu is not available: " + menu.getName());
        }

        Order order = Order.builder()
                .member(member)
                .seat(seat)
                .menu(menu)
                .quantity(request.getQuantity())
                .orderDateTime(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        order.setStatus(status);
        return OrderResponse.from(order);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
