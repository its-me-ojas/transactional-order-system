package com.example.transactional.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.transactional.model.Order;
import com.example.transactional.model.OrderStatus;
import com.example.transactional.model.Payment;
import com.example.transactional.model.PaymentStatus;
import com.example.transactional.repository.OrderRepository;
import com.example.transactional.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository,OrderRepository orderRepository, OrderService orderService){
        this.paymentRepository=paymentRepository;
        this.orderRepository=orderRepository;
        this.orderService = orderService;
    }

    @Transactional
    public Payment processPayment(UUID orderId,UUID idempotencyKey){
        // 1. Idempotency Check
        // if we already processed this key, return the OLD Result
        // INTERVIEW TIP: Usuallly we'd return the saved payment, but for simplicity we will check existence

        if (paymentRepository.findByIdempotencyKey(idempotencyKey).isPresent()){
            throw new RuntimeException("Payment already processed (Idempotennt Request");
        }
        // 2. Pessimistic Lock 
        // this line STOPS other threads here
        Order order = orderRepository.findByIdWithLock(orderId).orElseThrow(()-> new RuntimeException("Order not found"));

        // 3. Validation (Business Logic)
        if (order.getStatus()!=OrderStatus.CREATED){
            throw new RuntimeException("Order cannot be paid. Current status: "+ order.getStatus());
        }

        // 4. Create payment (The action)
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setIdempotencyKey(idempotencyKey);
        payment.setAmount(order.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);

        // 5. Update order
        order.setStatus(OrderStatus.PAID);

        // we dont need orderResository.save(order) explicitly because 
        // HIBERNATE "Dirty Checking" detects the change and updates 
        // it automatically at commit.
        return paymentRepository.save(payment);
        
    }
}
