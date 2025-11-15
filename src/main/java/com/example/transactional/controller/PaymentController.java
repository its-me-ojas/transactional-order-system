package com.example.transactional.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.transactional.model.Payment;
import com.example.transactional.service.PaymentService;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService= paymentService;
    }

    @PostMapping
    public Payment makePayment(@RequestHeader("Idempotency-Key") UUID idempotencyKey,@RequestParam UUID orderId) {
        return paymentService.processPayment(orderId, idempotencyKey);
    }
    
    
}
