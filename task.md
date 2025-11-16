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
- [x] Global Exception Handling (`@ControllerAdvice`)

## Verification
- [x] Manual Verification (Curl)
- [ ] Unit Test: State Transitions
- [x] Integration Test: Concurrent Payments (Shell Script)
