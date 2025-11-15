package com.example.transactional.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.transactional.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment,UUID>{
    Optional<Payment> findByIdempotencyKey(UUID idempotencyKey);
}
