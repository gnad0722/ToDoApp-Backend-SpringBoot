# ToDo Management Application — Backend

A RESTful backend for managing personal to-do lists, tasks, notes, and projects, built with **Spring Boot 4** and **Spring Modulith**. It provides JWT authentication with refresh tokens, password reset via email OTP, Redis caching, dynamic filtering with pagination, and a daily email reminder for scheduled to-do lists.

## Features

- **Authentication & account**
  - Register (username, email, password, birthday) and log in with email + password.
  - Short-lived JWT access token plus a refresh token; refresh and logout endpoints.
  - Forgot password flow: OTP sent by email → verify OTP → reset password with a one-time reset token.
  - View and update the current user's profile (username, birthday).
- **To-do lists** — CRUD, list the current user's lists or a user's lists by ID, with filtering and pagination.
- **Tasks** — CRUD inside a to-do list, expected start/end times, mark as done, filtering and pagination.
- **Notes** — one note per task: create, read, update, delete by task ID.
- **Projects** — CRUD and the current user's projects with filtering and pagination.
- **Daily reminders** — a scheduled job runs every day at 00:00 (Asia/Bangkok) and emails each user the to-do lists starting that day.
- **Business rules** — due dates cannot be in the past, end time must be after start time, birthday cannot be in the future.
- **Caching** — users, to-do lists, tasks, notes, projects, and tokens are cached in Redis with configurable TTLs.
- **Consistent responses** — every response (including errors and security errors) uses the same `success` / `data` / `message` envelope.

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 25 (Kotlin plugin configured with Lombok support) |
| Framework | Spring Boot 4.1, Spring Modulith 2.1 |
| Web | Spring Web MVC, Jakarta Bean Validation |
| Security | Spring Security, OAuth2 Resource Server (JWT), BCrypt |
| Persistence | Spring Data JPA / Hibernate, PostgreSQL 17 |
| Cache | Redis (Spring Data Redis) |
| Email | Spring Boot Mail (SMTP) |
| API docs | springdoc-openapi (Swagger UI) |
| Build / infra | Maven (wrapper included), Docker Compose |

## Project Structure

The application is a modular monolith. Business features live in `module/`, shared infrastructure in `common/`.

```text
src/main/java/dev/gnad/SpringModulith/
├── common/
│   ├── api/          # ApiResponse, PageResponse, exceptions, global exception handler
│   ├── cache/        # Redis cache configuration & serialization strategies per cache
│   ├── config/       # Security, JWT, OpenAPI, scheduling configuration
│   ├── email/        # Email sender and email templates
│   ├── otp/          # OTP generation and verification
│   ├── policy/       # Shared validation rules (time range, due date, birthday)
│   ├── schedule/     # Reminder schedules and the daily cron job
│   ├── security/     # Token services, 401/403 handlers
│   ├── token/        # Refresh token & reset-password token (factory + strategy)
│   └── utility/filter/  # Dynamic filter parsing -> JPA Specifications
└── module/
    ├── Auth/         # Register, login, tokens, password reset, profile
    ├── ToDoList/
    ├── Task/
    ├── Note/
    └── Project/
```

Each module follows the same layout: `controller/`, `domain/` (entities, DTOs), `internal/repository/`, and `service/`.

## Getting Started

### Prerequisites

- JDK 25
- Docker and Docker Compose (for PostgreSQL and Redis)
- An SMTP account for sending emails (e.g. a Gmail app password)

### 1. Start PostgreSQL and Redis

```bash
docker compose up -d
```

This starts:

- PostgreSQL 17 on `localhost:5433` (database `ToDo`)
- Redis on `localhost:6379`

The `spring-boot-docker-compose` dependency can also start these containers automatically when the application runs.

### 2. Configure the application

Edit `src/main/resources/application.yml` and set your own values:

| Property | Description |
|---|---|
| `spring.datasource.url` / `username` / `password` | PostgreSQL connection |
| `spring.redis.host` / `port` | Redis connection |
| `spring.cache.ttl-user-data` | TTL in minutes for user-related caches |
| `spring.cache.ttl-reference-data` | TTL in minutes for to-do / task / note / project caches |
| `spring.mail.username` / `password` | SMTP account used to send OTP and reminder emails |
| `security.jwt.secret` | Secret key for signing access tokens |
| `security.jwt.issuer` | JWT issuer |
| `security.jwt.access-token-minutes` | Access token lifetime (minutes) |
| `security.jwt.refresh-token-expiration` | Refresh token lifetime (milliseconds) |
| `security.jwt.reset-password` | Reset-password token settings |

> Do not commit real passwords or secrets. Prefer environment variables, e.g. `password: ${MAIL_PASSWORD}`.

Hibernate runs with `ddl-auto: update`, so tables are created automatically on startup.

### 3. Run the application

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

The server starts on `http://localhost:8080`.

### Run tests

```bash
./mvnw test
```

## API Documentation

With the application running, open Swagger UI:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Click **Authorize** and paste an access token to call protected endpoints.

## Authentication

All endpoints require a bearer token except register, login, refresh, forgot-password, verify-otp, reset-password, and the Swagger/OpenAPI paths.

```http
Authorization: Bearer <access_token>
```

### Register — `POST /api/auth/register`

```json
{
  "username": "demo_user",
  "email": "demo@example.com",
  "password": "ChangeMe123",
  "birthday": "2000-01-01T00:00:00"
}
```

Username ≥ 4 characters, password 6–72 characters, valid email, birthday required and not in the future.

### Login — `POST /api/auth/login`

```json
{ "email": "demo@example.com", "password": "ChangeMe123" }
```

Returns `accessToken`, `refreshToken`, and `tokenType`.

### Refresh / Logout — `POST /api/auth/refresh`, `POST /api/auth/logout`

```json
{ "rawToken": "<refresh_token>" }
```

Refresh returns a new token pair; logout revokes the refresh token.

### Forgot password

1. `POST /api/auth/forgot-password` with `{ "email": "demo@example.com" }` — sends an OTP to the email.
2. `POST /api/auth/verify-otp` with `{ "email": "demo@example.com", "otp": "123456" }` — returns a reset-password `token`.
3. `POST /api/auth/reset-password` with:

```json
{ "newPassword": "NewPass123", "resetPasswordToken": "<token>" }
```

## API Endpoints

### Auth — `/api/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | — | Register a user |
| `POST` | `/api/auth/login` | — | Log in, get access + refresh tokens |
| `POST` | `/api/auth/refresh` | — | Exchange a refresh token for new tokens |
| `POST` | `/api/auth/logout` | ✔ | Revoke a refresh token |
| `POST` | `/api/auth/forgot-password` | — | Send an OTP to the user's email |
| `POST` | `/api/auth/verify-otp` | — | Verify OTP, get a reset-password token |
| `POST` | `/api/auth/reset-password` | — | Set a new password |
| `GET` | `/api/auth/me` | ✔ | Get the current user's profile |
| `PATCH` | `/api/auth/update-profile` | ✔ | Update username and/or birthday |

### To-do lists — `/api/todo`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/todo/` | Create a to-do list (also creates its reminder schedule) |
| `GET` | `/api/todo/{id}` | Get a to-do list by ID |
| `PATCH` | `/api/todo/{id}` | Update a to-do list |
| `DELETE` | `/api/todo/{id}` | Delete a to-do list |
| `GET` | `/api/todo/me` | Current user's lists (filter + pagination) |
| `GET` | `/api/todo/user/{id}` | Lists of a user by ID (filter + pagination) |

```json
{ "topic": "Prepare for backend interview", "startTime": "2026-12-01T09:00:00" }
```

`topic` ≥ 10 characters, `startTime` required.

### Tasks — `/api/task`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/task` | Create a task in a to-do list |
| `GET` | `/api/task/{id}` | Get a task by ID |
| `PATCH` | `/api/task/{id}` | Update a task |
| `DELETE` | `/api/task/{id}` | Delete a task |
| `PATCH` | `/api/task/done/{id}` | Mark a task as completed |
| `GET` | `/api/task/todo/{id}` | Tasks of a to-do list (filter + pagination) |

```json
{
  "name": "Review Spring Security",
  "description": "Review JWT authentication and authorization",
  "toDoListId": 1,
  "expectStart": "2026-12-01T09:00:00",
  "expectEnd": "2026-12-01T10:30:00"
}
```

`name` ≥ 5 characters, `description` and `toDoListId` required, `expectEnd` must be after `expectStart`.

### Notes — `/api/note`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/note/` | Create a note for a task |
| `GET` | `/api/note/{taskId}` | Get the note of a task |
| `PUT` | `/api/note/{taskId}` | Update the note of a task |
| `DELETE` | `/api/note/{taskId}` | Delete the note of a task |

```json
{ "description": "Check the acceptance criteria before submitting.", "taskId": 1 }
```

`description` ≥ 10 characters, `taskId` required.

### Projects — `/api/project`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/project/` | Create a project |
| `GET` | `/api/project/{id}` | Get a project by ID |
| `PATCH` | `/api/project/{id}` | Update a project |
| `DELETE` | `/api/project/{id}` | Delete a project |
| `GET` | `/api/project/me` | Current user's projects (filter + pagination) |

```json
{
  "topic": "Backend Learning Plan",
  "description": "Organize backend development learning goals and milestones.",
  "startTime": "2026-12-01T09:00:00",
  "expectEnd": "2027-01-01T18:00:00"
}
```

`topic` ≥ 5 characters, `description` ≥ 20 characters.

## Filtering and Pagination

List endpoints accept standard Spring pagination (`page`, `size`, `sort`) and one or more filter strings in the form:

```text
field:OPERATOR:value
```

The query parameter is `filters` for `/api/todo/me`, `/api/todo/user/{id}`, and `/api/task/todo/{id}`, and `filter` for `/api/project/me`. Multiple filters are combined with AND.

**Operators:** `EQ`, `NOT_EQ`, `CONTAINS`, `STARTS_WITH`, `ENDS_WITH`, `GT`, `GTE`, `LT`, `LTE`, `IS_NULL`, `IS_NOT_NULL` (case-insensitive).

**Allowed fields:**

| Resource | Fields |
|---|---|
| To-do list | `topic`, `startTime`, `numTask`, `numCompleteTask` |
| Task | `name`, `description`, `isDone`, `expectStart`, `expectEnd` |
| Project | `topic`, `description`, `startTime`, `expectEnd`, `numTask`, `numCompleteTask` |

Example:

```http
GET /api/task/todo/1?filters=isDone:EQ:false&filters=name:CONTAINS:spring&page=0&size=10&sort=expectStart,asc
```

An unknown field or malformed filter returns `400 Bad Request`.

## Response Format

Every response uses the same envelope:

```json
{
  "success": true,
  "data": { "id": 1 },
  "message": "Login successfully"
}
```

Paginated responses put the items and metadata inside `data`:

```json
{
  "success": true,
  "data": {
    "content": [],
    "meta": { "page": 0, "size": 10, "totalElements": 0, "totalPages": 0 }
  },
  "message": null
}
```

Errors use `success: false` with a message; validation errors include a field → message map in `data`. Typical status codes: `400` (validation / business rule), `401` (missing or invalid token), `403` (forbidden), `404` (not found), `409` (conflict).

## Typical Workflow

1. Register with `POST /api/auth/register` and log in with `POST /api/auth/login`.
2. Use the access token as a bearer token; refresh it with `POST /api/auth/refresh` when it expires.
3. Create a to-do list with `POST /api/todo/` — a reminder email is sent at midnight on its start day.
4. Add tasks with `POST /api/task`, optionally attach a note with `POST /api/note/`.
5. Mark tasks done with `PATCH /api/task/done/{id}`.
6. Browse lists, tasks, and projects using filters and pagination.

## License

No license has been chosen yet. Add a `LICENSE` file and update this section before publishing the project.
