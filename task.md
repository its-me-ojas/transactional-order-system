# Project 3: Transactional Order & Payment Processing System

## Plan & Setup
- [x] Define Execution Plan
- [x] Initialize Spring Boot Project (Manual Setup Guide)

## Core Domain Layer
- [x] Implement Order Entity (`CREATED`, `PAID`, `CANCELLED` states)
- [x] Implement Payment Entity (`idempotencyKey`)
- [x] Implement Repositories

## Business Logic & Transactions
- [x] Implement `OrderService` (State transitions)
- [x] Implement `PaymentService` (Transactional & Pessimistic Locking)
- [x] Implement Idempotency Logic

## API Layer
- [x] Create `OrderController`
- [x] Create `PaymentController` (Handle `Idempotency-Key` header)

## Verification
- [ ] Unit Test: State Transitions
- [ ] Integration Test: Concurrent Payments
