# Transactional System - Interview Doubts & Concepts

This file tracks every question you asked and the "Interview-Ready" answer.

## 1. Spring Initializr vs Manual Setup
**Q:** Why did we write `pom.xml` manually instead of using start.spring.io?
**A:** To understand **Dependency Management**.
- **Magical Black Box**: Initializr hides the complexity.
- **Parent POM**: You saw how `<parent>` manages logic for you (e.g., compatible versions of Hibernate/Jackson).
- **Starters**: You learned that `spring-boot-starter-web` bundles Tomcat + Spring MVC.

## 2. Lombok: `@Data` vs `@Getter/@Setter`
**Q:** Should I use default `@Data` on Entities?
**A:** **NO.**
- **The Trap**: `@Data` generates `toString()`, `equals()`, and `hashCode()` using ALL fields.
- **The Crash**: If Order has a list of Payments, and Payment has an Order, `toString()` calls each other in an infinite loop -> `StackOverflowError`.
- **The Fix**: Use `@Getter`, `@Setter`, `@NoArgsConstructor`.

## 3. JPA Lifecycle Annotations
**Q:** Explain `@Version`, `@PrePersist`, `@PreUpdate`.
**A:**
- **`@Version` (Optimistic Locking)**:
  - **Concept**: Adds a version number column.
  - **Scenario**: Alice and Bob both read Version 1. Alice saves (Version becomes 2). Bob tries to save Version 1 -> **Fails**.
  - **Why**: Prevents "Lost Updates" without expensive database locks.
- **`@PrePersist`**:
  - **When**: Runs once, right before the SQL `INSERT`.
  - **Use**: Set `createdAt`, default `Status`.
- **`@PreUpdate`**:
  - **When**: Runs right before SQL `UPDATE`.
  - **Use**: Update `updatedAt`.

## 4. `FetchType.LAZY`
**Q:** Why use `FetchType.LAZY` in `@ManyToOne`?
**A:**
- **EAGER (Default for Has-One)**: 
  - Loading a `Payment` **ALWAYS** joins and loads the `Order`.
  - If you fetch 1,000 Payments, you fetch 1,000 Orders (Massive overhead).
- **LAZY**:
  - Loading a `Payment` leaves `Order` as a **Proxy** (empty shell).
  - The SQL for Order is only fired **IF** you call `payment.getOrder().getAmount()`.
- **Interview Rule**: "Always default to LAZY for relationships to prevent memory bloat."

## 5. Idempotency Key (The "Double Charge" Killer)
**Q:** Why is `idempotencyKey` necessary?
**A:**
- **The Scenario**:
  1. User clicks "Pay $50".
  2. Server processes payment (Money gone from bank).
  3. **Network Failure**: The Success Response never reaches the user's phone.
  4. User sees "Loading..." then "Timeout".
  5. User clicks "Pay $50" **AGAIN**.
- **Without Key**: Server charges $50 again. (User furious).
- **With Key**:
  1. Phone generates random connection ID (UUID) *before* sending.
  2. Server sees Key: "Oh, I already processed this Key 5 seconds ago."
  3. Server returns the **previous** success message. NO new charge.
- **Rule**: "Network retries are safe ONLY if you have Idempotency."

## 6. Repository Interfaces & `@Query`
**Q:** Why do we write functions inside an **Interface**? Where is the code?
**A:**
- **Magic**: You don't write the code. Spring writes it for you at Runtime.
- **How**: Spring creates a "Proxy" class that implements your interface.
- **@Query**: When you call `findByIdWithLock`, the Proxy sees the annotation, reads the SQL `SELECT ... FOR UPDATE`, and runs it.
- **Why do this?**:
  1.  Saves boilerplate (No manually writing JDBC code).
  2.  `@Lock` works automatically implies `SELECT ... FOR UPDATE`.

## 7. Pessimistic vs Optimistic Locking (The Interview Winner)
**Q:** We added `@Version` (Optimistic) to Order. Why do we *also* need `@Lock(PESSIMISTIC_WRITE)` in the Repo?
**A:**
- **Optimistic (`@Version`)**:
  - **Philosophy**: "I hope nobody else touches this."
  - **Mechanism**: Checks version number at **Commit time**.
  - **Use Case**: Admin updating order details. High concurrency reads, low writes.
  - **Limitation**: It allows multiple threads to *start* processing, only failing at the very end.
- **Pessimistic (`SELECT ... FOR UPDATE`)**:
  - **Philosophy**: "I assume everyone is trying to break this."
  - **Mechanism**: locks the row in DB at **Read time**.
  - **Use Case**: **PAYMENTS**. We cannot afford to let two threads *start* processing a payment. We must block the second one immediately.
- **Summary**: Use Pessimistic for Money/Inventory. Use Optimistic for User Profiles/Status Updates.

## 8. `@Transactional` (The Safety Net)
**Q:** Why did we put `@Transactional` on `createOrder`?
**A:**
- **The Code**: `return orderRepository.save(order);`
- **Without `@Transactional`**:
  - If you had 2 steps (Save Order -> Save Log) and Step 2 failed... Step 1 would stay in the DB.
  - **Data Corruption**. You have a "Zombie Order".
- **With `@Transactional`**:
  - It wraps everything in `BEGIN` and `COMMIT`.
  - If **ANYTHING** fails (Step 2 crashes), it runs `ROLLBACK`.
  - Step 1 is undone. The DB creates the illusion that nothing happened. "All or Nothing."

## 9. Hibernate Dirty Checking
**Q:** Why didn't we call `orderRepository.save(order)` after changing the status?
**A:**
- **Magic**: Hibernate keeps a "Snapshot" of every object it loaded.
- ** The Process**:
  1. You loaded `Order` (Status: CREATED). Hibernate takes a photo.
  2. You changed `order.setStatus(PAID)`.
  3. Method ends (`@Transactional` commits).
  4. Hibernate compares Current Object vs Snapshot.
  5. **Diff Found**: "Status changed from CREATED to PAID".
  6. **Auto-Action**: Hibernate generates and runs `UPDATE orders SET status = 'PAID'...` automatically.
- **Benefit**: You don't need to spam `.save()`. It prevents redundant updates.
