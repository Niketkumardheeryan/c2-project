# Data Model

## Enums

### TicketStatus

`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`

### TicketPriority

`LOW`, `MEDIUM`, `HIGH`

## Entities

### Ticket

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK | Generated |
| title | String | Not blank, max 200 | |
| description | String | Not blank, max 5000 | |
| status | TicketStatus | Not null | Default OPEN |
| priority | TicketPriority | Not null | Default MEDIUM |
| assignee | String | Nullable, max 120 | |
| createdAt | Instant | Not null | Set on create |
| updatedAt | Instant | Not null | Updated on change |

### Comment

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK | Generated |
| ticket | Ticket | FK, not null | Many comments → one ticket |
| author | String | Not blank, max 120 | |
| body | String | Not blank, max 2000 | |
| createdAt | Instant | Not null | |

## Relationships

- Ticket 1—* Comment (cascade delete comments with ticket)

## Persistence notes

- H2 file DB so data survives restart (`jdbc:h2:file:./data/tickets`)
- `ddl-auto=update` for local; no credentials in repo (use env vars for Postgres profile)
- Index on `status` for filter; optional index on title for search (LIKE is acceptable for assessment scale)
