# Implementation Plan - Transactional Order & Payment System

## Goal Description
Build a robust, transactional backend service in Java (Spring Boot) that handles Orders and Payments with strict correctness, idempotency, and concurrency control.

## User Review Required
> [!IMPORTANT]
> The user wants to type the code themselves. I will provide snippets and explanations. I will NOT auto-generate the full codebase files unless explicitly asked to fill in boilerplate.

## Proposed Changes
We will build the system layer by layer.

### 1. Project Initialization
- Spring Boot 3.x
- PostgreSQL Driver
- Spring Data JPA
- Validation API

### 2. Domain Layer (Entities)
- `Order.java`: Optimistic locking (`@Version`), Enum based status.
- `Payment.java`: Immutable payment records, links to Order.

### 3. Repository Layer
- `OrderRepository`: Custom query with `PESSIMISTIC_WRITE` lock for payment processing.

### 4. Service Layer (The Core)
- `@Transactional` annotations are key here.
- `processPayment(orderId, amount, idempotencyKey)`:
    1. Lock Order.
    2. Check Idempotency.
    3. Validate State (Order must be `CREATED`).
    4. Create Payment.
    5. Update Order Status.
    6. Returns result.

### 5. API Layer
- REST Controllers.
- Exception handling for `OptimisticLockingFailureException` or `PessimisticLockingFailureException`.

## Verification Plan
Since the user is coding, verification will involve:
1.  **Manual Testing**: using `curl` or Postman.
2.  **Concurrency Testing**: We can write a small shell script or Java test to fire parallel requests.
