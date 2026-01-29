# 🚀 Expense Tracker - Monolith

A **secure, scalable, microservice-ready application** for tracking personal expenses, built using **Spring Boot** backend with **JWT Authentication** and **Clean Architecture principles**.

This system allows users to:

✅ Register & login securely
✅ Create categories
✅ Track expenses
✅ Filter expenses by date/category
✅ Generate financial reports
✅ View monthly totals
✅ Analyze category spending

Designed with **future microservice migration in mind**.

---

## 📋 Table of Contents

- [Backend](#-backend)
- [Frontend](#-frontend)

---

# 🔧 Backend

## Overview

A **secure, scalable, microservice-ready backend** for tracking personal expenses, built using **Spring Boot**, **JWT Authentication**, and **Clean Architecture principles**.

---

# 🏗️ Backend Architecture

The application follows a **layered modular architecture**:

```
Controller → Service Interface → Service Implementation → Repository → Database
```

### Why this matters:

- Loose coupling
- Easier testing
- Replaceable implementations
- Microservice-ready

Each module is isolated:

```
auth
user
category
expense
reporting
```

No controller talks directly to repositories.

---

# 🔐 Backend Security

The API uses **JWT-based authentication**.

### Flow:

1. User logs in.
2. Server generates JWT token.
3. Client sends token in header:

```
Authorization: Bearer <token>
```

4. Spring Security validates token via filter.
5. User is stored in `SecurityContext`.

All endpoints except `/api/auth/**` require authentication.

---

# 🧱 Backend Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **JWT**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL / MySQL (plug & play)**
- **Lombok**
- **OpenAPI / Swagger**

---

# 📦 Backend Modules Overview

---

## ✅ Auth Module

Handles:

- User registration
- Login
- JWT token generation
- Password encryption (BCrypt)

Endpoints:

```
POST /api/auth/register
POST /api/auth/login
```

---

## ✅ User Module

Provides:

```
GET /api/users/me
```

Returns currently authenticated user.

---

## ✅ Category Module

Categories are **user-scoped**.

Meaning:

👉 One user cannot access another user's categories.

Endpoints:

```
POST   /api/categories
GET    /api/categories
DELETE /api/categories/{id}
```

---

## ✅ Expense Module

Core of the application.

Each expense belongs to:

```
User → Category → Expense
```

Endpoints:

```
POST   /api/expenses
PUT    /api/expenses/{id}
GET    /api/expenses/{id}
GET    /api/expenses
DELETE /api/expenses/{id}
```

---

### Filtering Supported

```
/api/expenses?categoryId=xxx
/api/expenses?from=2026-01-01&to=2026-01-31
/api/expenses?categoryId=xxx&from=2026-01-01&to=2026-01-31
```

---

## ✅ Reporting Module

Provides financial insights.

---

### Monthly Totals

```
GET /api/reports/monthly?year=2026
```

Returns:

```json
[
  { "month": 1, "total": 450.0 },
  { "month": 2, "total": 220.5 }
]
```

---

### Category Breakdown

```
GET /api/reports/category-breakdown?from=2026-01-01&to=2026-01-31
```

Returns:

```json
[
  {
    "categoryId": "uuid",
    "categoryName": "Food",
    "total": 300
  }
]
```

---

# 📊 Backend Database Design Philosophy

Instead of heavy entity relationships (`@ManyToOne`), this project stores:

```
userId
categoryId
```

### Why?

Because it is:

✅ microservice-friendly
✅ avoids tight coupling
✅ easier to scale
✅ prevents lazy-loading issues

Later, these IDs can map to external services.

---

# 📚 Backend API Documentation (Swagger)

Swagger UI is enabled using **springdoc-openapi**.

### Access:

```
http://localhost:8080/swagger-ui/index.html
```

### Authorize with JWT:

1. Login via `/api/auth/login`
2. Copy token
3. Click **Authorize 🔐**
4. Paste token

---

# ▶️ Running the Backend

## 1️⃣ Clone repo

```
git clone <repo-url>
```

---

## 2️⃣ Configure Database

Update:

```
application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_tracker
spring.datasource.username=postgres
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
```

---

## 3️⃣ Configure JWT

```properties
app.jwt.secret=VERY_LONG_RANDOM_SECRET_KEY_32CHARS_MIN
app.jwt.expiration-ms=86400000
```

---

## 4️⃣ Run App

```
mvn spring-boot:run
```

---

# 🔥 Backend Key Engineering Decisions

## ✔ Interface-driven services

Controllers depend on interfaces — not implementations.

Supports:

- Clean architecture
- Future distributed systems
- Easier mocking

---

## ✔ User-scoped data

Every query enforces ownership:

```
findByIdAndUserId()
```

Prevents data leaks.

---

## ✔ DTO-based API

Entities are never exposed.

Benefits:

- versioning safety
- better contracts
- decoupled persistence

---

## ✔ Aggregation via JPQL DTO projection

Reporting uses:

```java
SELECT new dto(...)
```

Which is:

✅ faster
✅ memory efficient
✅ production-grade

---

# 🚀 Future Backend Improvements (Already Architecture Ready)

This system can easily evolve into:

### 🔹 Microservices

- Auth Service
- Expense Service
- Reporting Service

### 🔹 Additions

- Redis caching
- Kafka events
- Budget tracking
- AI-based spending insights
- Multi-currency support
- Role-based admin dashboards

---

# ⭐ Why This Backend is Not a Beginner CRUD App

This project uses:

✅ layered architecture
✅ security
✅ aggregation
✅ ownership validation
✅ DTO projections
✅ modular design

This is closer to **real production backend engineering**.

---

# 💻 Frontend

Frontend documentation and setup instructions coming soon.

To be developed as a separate module (React, Vue, Angular, or similar).

### Planned Features:

✅ User-friendly dashboard
✅ Expense tracking UI
✅ Category management
✅ Report visualization
✅ Authentication flow
✅ Responsive design
