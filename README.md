# College Canteen Token System

A role-based token/ordering system for a college canteen, letting students place orders,
staff manage the queue, and admins monitor daily volume. Built as a 15-week DevOps
lifecycle project (Git → Jenkins CI/CD → Selenium → Docker → Ansible).

## Tech Stack

| Component | Choice |
|---|---|
| Backend | Java 17 + Spring Boot 3.x |
| Build Tool | Maven |
| Database | MySQL |
| Frontend | HTML/CSS/JS (Thymeleaf) or React |
| Deployment | Apache Tomcat, later Docker |
| CI/CD | Jenkins |
| Config Management | Ansible |

## Architecture

Layered architecture: `controller -> service -> repository -> model`, with `dto` for
request/response shaping and `security` for auth. See the Week 3 SRS/architecture doc
for the use-case and architecture diagrams, and the ERD below.

**Entities:** `User` (STUDENT/STAFF/ADMIN), `MenuItem`, `Token` (PENDING → PREPARING → READY → COLLECTED)

## Local Setup

**Prerequisites:** JDK 17+, Maven 3.9+, MySQL 8.x, Git

```bash
git clone https://github.com/<you>/canteen-token-system.git
cd canteen-token-system

# create the database
mysql -u root -p -e "CREATE DATABASE canteen_db;"

# set your DB password (or edit application.properties directly)
export DB_PASSWORD=<your_password>

mvn clean install
mvn spring-boot:run
```

App runs at `http://localhost:8082`.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register a new user |
| POST | /api/auth/login | Authenticate (in progress) |
| GET | /api/menu | List available menu items |
| POST | /api/menu | Add menu item (Admin) |
| POST | /api/tokens | Create a token/order (Student) |
| GET | /api/tokens/{id} | Get token details |
| GET | /api/tokens?studentId= | List a student's orders |
| GET | /api/tokens/queue | Active order queue (Staff) |
| PUT | /api/tokens/{id}/status | Update order status (Staff) |
| GET | /api/tokens/search?query= | Search by token ID or student name |
| GET | /api/dashboard/summary | Daily order summary (Admin) |

## Branching Convention

- `feature/<short-description>` - new features
- `bugfix/<short-description>` - bug fixes
- `release/<version>` - release baselines

## Project Status

MVP skeleton (Week 3-4). See the project board for sprint progress.

<!-- Jenkins CI verified -->