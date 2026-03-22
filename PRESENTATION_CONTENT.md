# Expense Tracker Application — Presentation Content

**Rewritten to match the actual application codebase**

---

# 🎯 Slide 1 — Title Slide

**Expense Tracker Application**
Software Engineering Project Presentation
Based on Week 1–7 Development Activities

**Student:** Yogesh Mahaseth  
**Course:** DEV 615  
**Instructor:** Prof. Som Prasad Shrestha  
**Date:** 2025/2026  

---

# 📌 Slide 2 — Project Overview

The Expense Tracker Application is a **full-stack web application** that helps users manage personal financial expenses efficiently.

**Architecture:**
- **Backend:** Spring Boot 4 REST API (Java 17)
- **Frontend:** React SPA with Vite and Tailwind CSS
- **Database:** H2 (development) / MySQL (production)

**Core capabilities:**
- Record and manage expenses with categories
- View dashboard summaries and reports
- Track monthly budget vs. spending
- Secure user authentication and data isolation
- REST API with Swagger documentation

The application was developed incrementally across Week 1–7 using layered architecture, JWT security, Spring Data JPA, JUnit testing, and K6 performance evaluation.

---

# 📌 Slide 3 — Development Progress (Week 1–7)

**Week 1:** Basic Spring Boot MVC setup with controller and view integration.

**Week 2:** Form handling and layered architecture (Controller → Service → Repository).

**Week 3:** REST API development with validation and JSON communication.

**Week 4:** Database integration using Spring Data JPA and H2/MySQL.

**Week 5:** JWT-based authentication and Spring Security implementation.

**Week 6:** Unit testing with JUnit and Mockito (controllers and services).

**Week 7:** Performance testing using K6 load testing tool.

These stages reflect modern enterprise development practices.

---

# 📌 Slide 4 — System Architecture

The application follows a **Layered Architecture** with a **React SPA** frontend:

**Backend (Spring Boot):**
- **Controller Layer:** REST endpoints (Auth, User, Category, Expense, Reporting)
- **Service Layer:** Business logic (interfaces + implementations)
- **Repository Layer:** Spring Data JPA for database access
- **Database:** H2 (dev) / MySQL (prod) with indexed tables

**Frontend (React):**
- **SPA:** Vite + React Router
- **Auth:** AuthContext with JWT in localStorage
- **UI:** Tailwind CSS, responsive layout (desktop + mobile bottom nav)

**Modules:** `auth`, `user`, `category`, `expense`, `reporting`

This architecture improves maintainability, scalability, and separation of concerns.

---

# 📌 Slide 5 — Key Features of Expense Tracker

**Authentication & Users:**
- User registration and login
- JWT-based stateless authentication
- Role support (USER / ADMIN)
- Protected routes and API endpoints

**Expense Management:**
- Add, edit, delete expenses
- Filter by date range and category
- Input validation (Bean Validation)

**Categories:**
- User-scoped categories
- Create and delete categories

**Reporting:**
- Monthly totals by year
- Category breakdown by date range

**Budget:**
- Monthly budget tracking (localStorage)
- Progress bar vs. actual spending

**Technical:**
- REST API with Swagger (OpenAPI)
- CORS configured for frontend
- Database indexes for performance

---

# 📌 Slide 6 — Strengths of the Application

**Architecture & Design:**
- Modular layered architecture (Controller → Service → Repository)
- ID-based references for future microservice readiness
- DTOs and JPQL projections for clean API contracts

**Security:**
- JWT authentication with BCrypt password hashing
- Stateless session management
- User-scoped data isolation

**Quality:**
- Bean Validation reduces incorrect data entry
- JUnit tests for controllers and services
- K6 load testing with defined thresholds

**User Experience:**
- React SPA with responsive design
- Mobile-friendly bottom navigation
- Swagger API documentation

**Technology:**
- Spring Boot reduces configuration complexity
- Spring Data JPA simplifies persistence
- H2 console for development debugging

---

# 📌 Slide 7 — Weaknesses of the Application

**Functional Limitations:**
- Budget stored in localStorage (not persisted to backend)
- No recurring expense automation
- Limited financial analytics (no charts or advanced dashboards)
- No export to PDF/Excel

**Technical Limitations:**
- No cloud deployment implemented
- Role-based access control present but not fully utilized in UI
- No OAuth or multi-factor authentication
- Performance testing performed locally only

**Infrastructure:**
- No caching (Redis, etc.)
- No load balancing or horizontal scaling
- Single database instance

These weaknesses provide opportunities for future enhancement.

---

# 📌 Slide 8 — Testing and Quality Evaluation

**Unit Testing (JUnit + Mockito):**
- **AuthControllerTest:** Register (valid, invalid email, duplicate), login (valid, invalid)
- **CategoryControllerTest, ExpenseControllerTest, ReportingControllerTest:** API behavior with mocks
- **UserControllerTest:** Current user endpoint
- **Service Tests:** AuthService, CategoryService, ExpenseService (business logic)

**Smoke Test:**
- `ExpenseTrackerApplicationTests` — context load verification

**Performance Testing (K6):**
- **Scenario:** Register user → GET /users/me → GET/POST /categories → POST/GET /expenses → GET /reports/monthly
- **Load profile:** Ramp 0→5 VUs (30s), 10 VUs (1m), spike 20 VUs (30s), 10 VUs (1m), ramp down (30s)
- **Thresholds:** p95 &lt; 2s, error rate &lt; 5%

Testing ensures reliability, correctness, and scalability.

---

# 📌 Slide 9 — Critical Evaluation of Features

**Successfully demonstrated:**
- Full software lifecycle development
- Layered architecture with REST API
- JWT security integration
- Database design with indexes
- Unit testing strategies
- Performance evaluation with K6
- Modern frontend (React SPA) with responsive UI

**Real-world gaps:**
- Advanced analytics and visualization
- Cloud deployment and DevOps
- Stronger security (MFA, OAuth)
- Budget persistence and server-side logic
- Caching and scalability infrastructure

This evaluation highlights both learning outcomes and practical limitations for production use.

---

# 📌 Slide 10 — Suggested Improvements

**Functional Improvements:**
- Expense analytics dashboard with charts
- Server-side budget tracking and alerts
- Recurring expense automation
- Export reports (PDF / Excel)
- Income tracking alongside expenses

**Technical Improvements:**
- Cloud deployment (AWS / Azure)
- Microservices architecture (split by domain)
- OAuth or multi-factor authentication
- Full role-based access control (Admin vs User UI)
- API versioning

**Performance Improvements:**
- Caching (Redis) for reports and categories
- Database query optimization and indexing review
- Load balancing and horizontal scaling
- CDN for static frontend assets

These improvements would increase real-world usability and scalability.

---

# 📌 Slide 11 — Lessons Learned

**Backend:**
- Spring Boot layered architecture
- REST API design and validation
- Spring Data JPA and database integration
- JWT security implementation

**Frontend:**
- React SPA development with Vite
- AuthContext and protected routes
- Responsive design with Tailwind CSS

**Quality:**
- JUnit and Mockito for unit testing
- K6 for performance and load testing
- Threshold-based quality gates

**Design:**
- User-scoped data modeling
- DTOs and API contracts
- Enterprise software design practices

This project provided practical exposure to modern full-stack web application development.

---

# 📌 Slide 12 — Conclusion

The Expense Tracker Application demonstrates a complete software development lifecycle from basic architecture to security, testing, and performance evaluation.

**Delivered:**
- Spring Boot REST API with JWT auth
- React SPA with responsive UI
- JPA persistence with H2/MySQL
- JUnit tests and K6 load testing

The project successfully meets academic objectives while highlighting areas for future enhancement toward production-level systems.

---

# 📌 Slide 13 — Questions

Thank You  
Any Questions?

---

# ✅ Optional Additions

- PPT file ready to download
- UML diagrams (class, sequence, deployment)
- Architecture diagrams (layered, component)
- Speaker notes for presentation
- Shortened 10-minute version
