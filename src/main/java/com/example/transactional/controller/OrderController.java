package com.example.transactional.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactional.model.Order;
import com.example.transactional.service.OrderService;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService=orderService;
    }

    @PostMapping
    public Order createOrder(@RequestParam UUID userId,@RequestParam BigDecimal amount){
        return orderService.createOrder(userId, amount);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable UUID id){
        return orderService.getOrder(id);
    }
    
    
}
