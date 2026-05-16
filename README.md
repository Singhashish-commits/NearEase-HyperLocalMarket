# 🛠️ HyMarket: Next-Gen Service Marketplace API

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.1.4-6DB33F?style=for-the-badge)
![Redis](https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Deployed on Render](https://img.shields.io/badge/Deployed_on-Render-46E3B7?style=for-the-badge&logo=render)

HyMarket is an enterprise-grade backend infrastructure for a two-sided service marketplace (similar to UrbanCompany or TaskRabbit). It handles complex state management for bookings, secure Escrow-style payment routing, Redis-backed OTP handshakes, and features an intelligent RAG (Retrieval-Augmented Generation) Customer Support AI.

---

## ✨ Core Engineering Features

### 1. Escrow-Style Payment Lifecycle & Strict Refund Math
To protect both providers and customers, the platform holds funds in Escrow (`PAID_TO_PLATFORM`). Funds are only disbursed (`TRANSFERRED_TO_PROVIDER`) upon job completion via an OTP handshake.
* **Cancellation Engine:** Implements a strict time-based penalty algorithm. Cancellations > 2 hours prior yield a 100% refund; cancellations < 2 hours trigger an automatic 20% platform penalty, accurately tracked in the `payment_transactions` audit table.

### 2. Intelligent Support Bot (RAG Architecture)
Built with **Spring AI** and an in-memory Vector Store. Instead of generic LLM responses, the bot intercepts user queries, searches the platform's proprietary rules document (`platform-rules.txt`), and forces the OpenAI model to answer *strictly* based on marketplace policies.

### 3. Redis-Backed State Verification
Job completions and two-step booking cancellations are secured via transient OTPs cached in Redis, preventing state manipulation and ensuring physical presence/consent.

### 4. DTO-Driven Data Masking
Employs View-Specific DTOs (e.g., `PublicProviderProfileDto`) to completely separate public search results from sensitive PII, ensuring phone numbers and exact locations are never exposed to unauthenticated networks.

---

## 🏗️ Architecture & Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **AI Integration:** Spring AI, OpenAI API, SimpleVectorStore
* **Database:** PostgreSQL (Production on Render) / MySQL (Local)
* **Caching:** Redis
* **Security:** Spring Security & JWT Authentication

---

## 🚀 Quick Start (Local Setup)

### Prerequisites
* Java 17+ installed
* Maven installed
* A local Redis server running on port `6379`
* PostgreSQL or MySQL running locally

### 1. Clone the repository
```bash
git clone [https://github.com/yourusername/hymarket-backend.git](https://github.com/yourusername/hymarket-backend.git)
cd hymarket-backend

---

## ☁️ Production Deployment (Render)

This application is configured for seamless deployment on [Render](https://render.com). 

### 1. Build & Start Commands
When setting up the Web Service on Render, use the following commands:
* **Build Command:** `mvn clean package -DskipTests`
* **Start Command:** `java -jar target/hymarket-0.0.1-SNAPSHOT.jar` *(Note: ensure the jar name matches your `pom.xml` version)*

### 2. Production Environment Variables
In your Render Web Service dashboard, you must configure the following Environment Variables. Do **not** commit these to GitHub:

| Variable Name | Description | Example / Source |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Forces Spring to use production settings | `prod` |
| `SPRING_DATASOURCE_URL` | The internal DB URL provided by Render | `jdbc:postgresql://hostname/dbname` |
| `SPRING_DATASOURCE_USERNAME`| Database user | `render_user` |
| `SPRING_DATASOURCE_PASSWORD`| Database password | `********` |
| `SPRING_DATA_REDIS_HOST` | Render Redis Internal URL | `red-cxyz...` |
| `SPRING_DATA_REDIS_PORT` | Render Redis Port | `6379` |
| `SPRING_AI_OPENAI_API_KEY` | Your OpenAI Key | `sk-********` |
| `JWT_SECRET` | Secure key for token generation | `your_long_production_secret` |

*Note: In production, `spring.jpa.hibernate.ddl-auto` should be set to `validate` or `none`, relying on tools like Flyway/Liquibase for schema migrations.*