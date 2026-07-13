# 🎟️ Event Ticketing System

[![backend-ci](https://github.com/EcjTn/event-ticketing-system/actions/workflows/maven.yml/badge.svg)](https://github.com/EcjTn/event-ticketing-system/actions/workflows/maven.yml)

still in development.

A full-stack event ticketing platform where users can browse events, grab tickets, and pay — all backed by a modular, event-driven backend built for real-world scale problems like double-booking, race conditions, and payment safety.

The frontend is kept simple on purpose. This project is a **backend-focused** build, meant to show real system design decisions, not just CRUD.

---

## 🧱 Tech Stack

**Frontend:** React (TypeScript)

**Backend:** Spring Boot, Spring Security, Spring Modulith

**Database & Storage:** PostgreSQL, Flyway, Redis, Cloudinary

**Payments:** Stripe (Java SDK)

**Other Tools:** Apache Tika, JUnit & Mockito

---

## 🏛️ Architecture Highlights

### Modular Monolith (Spring Modulith)
The backend is split into clean modules (Events, Orders, Payments, etc.) that **don't** share database relationships or foreign keys across modules. No module reaches directly into another module's tables.

- Modules talk to each other through **Application Events**, not direct calls.
- If a module truly needs another module's logic, that module exposes a **public interface (facade)** at the top level. Everything else lives in an `internal/` folder, hidden from other modules.
- This keeps modules independent, testable, and easy to reason about — closer to microservices, without the network overhead.

### Reliable Events (No Lost Messages)
Using `@ApplicationModuleListener` for transaction-aware events, paired with Spring Modulith's **event publication registry** (a JDBC-backed outbox pattern). A scheduled job re-sends events that failed to complete and clears out ones that finished successfully. This means an event is never silently dropped, even if something crashes mid-process.

### Race Conditions & Locking
Ticket inventory uses **pessimistic locking** (chosen over optimistic locking, expecting high traffic and lots of competing requests for the same tickets).

While building this, I hit a **deadlock** — caused by two requests locking the same set of ticket tiers in a different order. Fixed by always **sorting ticket tier IDs before locking**, so every request grabs locks in the same order.

### Expired Order Cleanup
A scheduled batch job cleans up expired, unpaid orders using PostgreSQL's `FOR UPDATE SKIP LOCKED` (so multiple workers can run this safely without stepping on each other) combined with `JdbcTemplate` batch updates. This:

- Cancels the related Stripe payment intent
- Restores ticket stock safely
- Skips rows already locked by another process
- Cuts down on database round trips

### Payments (Stripe)
Payment handling is fully **event-driven**. Creating an order fires an application event, which triggers Stripe payment intent creation as a side effect — keeping the Orders module and the Payments module decoupled.

Every Stripe PaymentIntent is created with an **idempotency key tied to the order ID**, so if a network retry happens, the user is never double-charged.

### Caching Strategy (Redis)
- **Reads:** Cache-Aside
- **Updates:** Write-Around with eviction
- **Safety net:** TTL-based expiration, so stale data can't live forever even if something goes wrong

### File Uploads
Images are checked with **Apache Tika** to confirm they're actually image files (not just trusting the file extension), then uploaded to **Cloudinary** using input streams — no files are ever saved to local disk.

### Security
Uses **Spring Security's built-in login and CSRF protection** rather than rolling custom auth — this let me focus on the actual product logic instead of reinventing login flows. Also uses Spring Security 7's `.spa()` CSRF support, which handles deferred CSRF tokens the way single-page apps need.

### Other Design Choices
- **Denormalized columns** in specific spots — for example, the Order table stores the event name directly, so it doesn't need a join just to show order history. This is a deliberate trade-off for speed and simpler reads.
- **Error responses** follow the **RFC 9457** standard (Problem Details for HTTP APIs), so errors are consistent and predictable across the whole API.

---

## ✨ Features

### For Everyone
- Browse upcoming events
- Pick a ticket tier (see below) and reserve it
- Pay for a reservation before it expires — unpaid reservations are automatically cancelled and the tickets go back into stock
- View full order history (completed, pending, cancelled)

### General Admission Tickets
Tickets are **tier-based**, not seat-based. A tier grants access to a zone (like VIP, FLOOR, or GENERAL) rather than assigning a specific seat.

### For Admins (Role-Based Access)
Simple role-based access control with two roles: **Admin** and **Customer**.

Admins can:
- Create events and set their details
- Create ticket tiers and prices for an event
- View, cancel, or modify any user's order
- Validate a ticket by its unique code and mark it as used, at the door

---

## 🧠 What This Project Demonstrates

- Designing a modular monolith that could realistically be split into microservices later
- Handling concurrency and race conditions safely, including debugging and fixing a real deadlock
- Building payment flows that survive retries and failures without double-charging anyone
- Using events to keep the system reliable, not just decoupled
- Making deliberate trade-offs (like denormalization) instead of "just following the rules"

---

## 📌 Notes
- This is still the initial Readme
- This project is meant as a portfolio piece to show backend architecture and problem-solving, not a production-ready ticketing platform. Stripe is running in test/sandbox mode.
