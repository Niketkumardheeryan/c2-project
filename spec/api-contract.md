# API Contract

Base path: `/api/v1`

## Error body

| Field | Type | Description |
|-------|------|-------------|
| status | int | HTTP status |
| message | string | Human-readable summary |
| fieldErrors | array | Optional `{ field, message }` for validation |

## Endpoints

### Create ticket

- **POST** `/api/v1/tickets`
- **Body:** `CreateTicketRequest`
  - `title` — `@NotBlank`, max 200
  - `description` — `@NotBlank`, max 5000
  - `priority` — optional enum; default MEDIUM
  - `assignee` — optional, max 120
- **Success:** `201` + `TicketResponse`
- **Errors:** `400` validation

### List / search / filter tickets

- **GET** `/api/v1/tickets?q={keyword}&status={status}`
- `q` — optional; case-insensitive match on title OR description
- `status` — optional exact status filter
- **Success:** `200` + `TicketSummaryResponse[]`

### Get ticket

- **GET** `/api/v1/tickets/{id}`
- **Success:** `200` + `TicketDetailResponse` (includes comments)
- **Errors:** `404`

### Update ticket fields

- **PATCH** `/api/v1/tickets/{id}`
- **Body:** `UpdateTicketRequest` (all fields optional; at least one required)
  - `title`, `description`, `priority`, `assignee`
- Does **not** change status (use transition endpoint)
- **Success:** `200` + `TicketResponse`
- **Errors:** `400`, `404`

### Transition status

- **POST** `/api/v1/tickets/{id}/transitions`
- **Body:** `TransitionTicketRequest` — `toStatus` required
- **Success:** `200` + `TicketResponse`
- **Errors:** `400` invalid transition / validation, `404`

### Add comment

- **POST** `/api/v1/tickets/{id}/comments`
- **Body:** `CreateCommentRequest` — `author` `@NotBlank`, `body` `@NotBlank`
- **Success:** `201` + `CommentResponse`
- **Errors:** `400`, `404`

## DTOs (shape)

### TicketResponse / TicketSummaryResponse

`id`, `title`, `description`, `status`, `priority`, `assignee`, `createdAt`, `updatedAt`

### TicketDetailResponse

Same as ticket + `comments: CommentResponse[]`

### CommentResponse

`id`, `author`, `body`, `createdAt`
