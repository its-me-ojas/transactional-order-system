package com.example.transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.transactional.model.Order;
import com.example.transactional.model.OrderStatus;
import com.example.transactional.repository.OrderRepository;
import com.example.transactional.service.OrderService;
import com.example.transactional.service.PaymentService;

@SpringBootTest
@AutoConfigureTestDatabase
public class ConcurrencyIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPreventDoublePayment_WhenExecuteConcurrently() throws InterruptedException {
        // Create a fresh order
        UUID userId = UUID.randomUUID();
        Order order = orderService.createOrder(userId, new BigDecimal("100.00"));

        // fire 10 concurrent payment requests
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    paymentService.processPayment(order.getId(), UUID.randomUUID());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        endLatch.await();

        System.out.println("Successes: " + successCount.get());
        System.out.println("Failures: " + failureCount.get());
        assertEquals(successCount.get(), 1); // Only 1 should pass
        assertEquals(failureCount.get(), threadCount - 1); // 9 should be blocked
        Order finalOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(finalOrder.getStatus(), OrderStatus.PAID);

    }

}
