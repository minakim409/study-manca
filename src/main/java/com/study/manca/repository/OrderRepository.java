package com.study.manca.repository;

import com.study.manca.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberId(Long memberId);

    List<Order> findBySeatId(Long seatId);

    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByMemberIdAndStatus(Long memberId, Order.OrderStatus status);
}
