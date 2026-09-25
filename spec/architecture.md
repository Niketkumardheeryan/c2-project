# Architecture

## Overview

Monorepo: Spring Boot REST API + React SPA. Browser talks only to `/api/v1/**`. Backend owns validation, persistence, and status transitions.

## Layers (backend)

- `controller` — HTTP adapters, `@Valid`
- `service` — business logic + state machine
- `repository` — Spring Data JPA
- `dto` — request/response records
- `domain` — entities, enums
- `config` — CORS, OpenAPI optional
- `exception` — `@RestControllerAdvice` + problem responses

## Components

| Component | Responsibility |
|-----------|----------------|
| `TicketController` | CRUD-ish ticket HTTP API |
| `CommentController` or nested routes | Add/list comments via ticket |
| `TicketService` | Create/update/search/filter + transitions |
| `TicketStatusTransitionService` | Allowed-edge map; reject illegal moves |
| `TicketRepository` / `CommentRepository` | JPA persistence |
| Frontend app | List, detail, forms, errors |

## Request flow

```
UI → REST Controller (@Valid DTO) → TicketService → (transition rules) → Repository → DB
```

## External dependencies

- Runtime DB: H2 file (`./data/tickets`) by default; `postgres` Spring profile for PostgreSQL
- Frontend dev proxy or CORS to backend `http://localhost:8080`

## Repo layout

```
backend/     Spring Boot (Maven)
frontend/    React (Vite + TypeScript)
spec/        Engineering specs
docs/        Prompt history index
.cursor/     Rules, skills, commands
```
