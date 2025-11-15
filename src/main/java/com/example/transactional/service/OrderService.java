package com.example.transactional.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.transactional.model.Order;
import com.example.transactional.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository= orderRepository;
    }

    @Transactional
    public Order createOrder(UUID userId, BigDecimal amount){
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        // status created set by @PrePersist
        return orderRepository.save(order);
    }

    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
