package com.example.transactional.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false,unique=true)
    private UUID idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id",nullable=false)
    private Order order;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
    }
    
}
