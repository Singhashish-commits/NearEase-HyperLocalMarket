#  NearEase — Hyperlocal Service Marketplace

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.1.4-6DB33F?style=for-the-badge)
![Redis](https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Deployed on Render](https://img.shields.io/badge/Deployed_on-Render-46E3B7?style=for-the-badge&logo=render)

# NearEase — Hyperlocal Service Marketplace

A production-deployed backend for a two-sided service marketplace connecting customers with local service providers. Built with Spring Boot and deployed on Render.

**Live API:** https://nearease-hyperlocalmarket.onrender.com/api/public/

---

## What this project does

NearEase allows customers to discover nearby service providers (plumbers, electricians, cleaners, etc.), book services, and pay securely. Providers manage their portfolio, accept bookings, and receive payouts only after verified job completion.

---

## Key engineering decisions

### Escrow-style payment flow
Payments are held by the platform (`PAID_TO_PLATFORM`) and only disbursed to providers (`TRANSFERRED_TO_PROVIDER`) after a job-completion OTP handshake — protecting both parties. A time-based cancellation engine applies a 20% platform penalty for late cancellations (under 2 hours), tracked in a `payment_transactions` audit table.

### Redis-backed OTP verification
Job completions and booking cancellations require transient OTPs stored in Redis with TTL-based expiry — preventing state manipulation and ensuring physical presence. Also used for user signup verification via Brevo (SMTP blocked on Render's free tier, so switched to Brevo API).

### Bloom filter for username validation
Probabilistic username uniqueness check using a Redis Bloom Filter — eliminates redundant DB reads on a high-frequency signup path.

### RAG-based support chatbot
Built with Spring AI and an in-memory Vector Store. The bot retrieves answers from a platform-rules document (`platform-rules.txt`) before querying OpenAI — responses are grounded in actual marketplace policies, not generic LLM output.

### DTO-driven data masking
View-specific DTOs (e.g., `PublicProviderProfileDto`) ensure phone numbers and exact locations are never returned to unauthenticated callers.

---

## Tech stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x, Java 17 |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| Caching | Redis (OTP, Bloom Filter) |
| AI | Spring AI, OpenAI API, SimpleVectorStore |
| Mail | Brevo (production), JavaMailSender (local) |
| Deployment | Render (app + managed PostgreSQL + Redis) |
| Containerization | Docker |

---

## API overview

**Auth**
```
POST /api/auth/signup
POST /api/auth/login
POST /api/auth/send-otp
POST /api/auth/validate-otp
```

**Provider**
```
POST   /api/provider/apply
POST   /api/provider/addService
GET    /api/provider/my-portfolio
GET    /api/provider/my/DashBoard
DELETE /api/provider/my-portfolio/{bookingId}/images
```

Full Swagger docs available at `/swagger-ui.html` when running locally.

---

## Performance Benchmarks

Load tested with JMeter (local environment):
- 500 concurrent users, 20s ramp-up
- Average latency: 8ms
- p95 latency: 11ms
- Max latency: 27ms
- Throughput: 25 req/sec
- Error rate: 0%

Environment: Spring Boot + PostgreSQL + Redis (local Docker)

## Running locally

**Prerequisites:** Java 17+, Maven, PostgreSQL, Redis on port 6379

```bash
git clone https://github.com/Singhashish-commits/NearEase-HyperLocalMarket.git
cd NearEase-HyperLocalMarket
```

Create a PostgreSQL database:
```sql
CREATE DATABASE nearease_db;
```

Set environment variables (or update `application.properties`):
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nearease_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_AI_OPENAI_API_KEY=your_openai_key
JWT_SECRET=your_secret_key
```

```bash
mvn spring-boot:run
```

---

## Deploying on Render

**Build command:** `mvn clean package -DskipTests`  
**Start command:** `java -jar target/hymarket-0.0.1-SNAPSHOT.jar`

Required environment variables on Render:

| Variable | Description |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` |
| `SPRING_DATASOURCE_URL` | Render internal DB URL |
| `SPRING_DATASOURCE_USERNAME` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `SPRING_DATA_REDIS_HOST` | Render Redis internal host |
| `SPRING_DATA_REDIS_PORT` | `6379` |
| `SPRING_AI_OPENAI_API_KEY` | OpenAI key |
| `JWT_SECRET` | Long random secret string |

---

## What I'd improve next

- Migrate to microservices — payment, booking, and notification as separate services
- Add real-time booking updates via WebSocket
- Replace SimpleVectorStore with a persistent vector DB (e.g., pgvector) for the AI chatbot
- Introduce Flyway for proper schema migration management