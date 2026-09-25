# Requirements

## Goal

Build a Support Ticket Management System so support teams can create, track, update, comment on, search, and filter tickets with a backend-enforced lifecycle.

## Scope

### In scope

- Create ticket (title, description, priority, optional assignee)
- List tickets
- View ticket details (including comments)
- Update title, description, priority, assignee
- Transition ticket status per state machine
- Add comments
- Search by keyword (title/description)
- Filter by status
- Persist in database (H2 file mode locally; PostgreSQL-ready profile)
- Backend Bean Validation
- UI with meaningful error messages
- State-machine integration tests

### Out of scope

- Authentication / authorization / multi-tenancy
- Email / Slack notifications
- File attachments
- Real-time websockets
- Admin user management (assignee is a free-text or simple string field)

## Acceptance criteria

- [x] Ticket can be created from UI
- [x] Tickets can be listed
- [x] Ticket details can be viewed
- [x] Ticket fields can be updated
- [x] Assignee can be changed
- [x] Comments can be added
- [x] Search works
- [x] Status filter works
- [x] Valid status transitions work
- [x] Invalid status transitions are rejected by backend
- [x] Data survives application restart (H2 file DB)
- [x] Backend validation works
- [x] UI shows meaningful errors
- [x] State-machine integration tests pass
- [x] No secrets are committed

## Assumptions

- Single shared board (no login)
- Assignee is a non-empty optional string (e.g. email or display name)
- Keyword search is case-insensitive contains on title and description
- Default status on create is `OPEN`
- Default priority on create is `MEDIUM` if omitted
- Stack: Java 21, Spring Boot 3.x, H2 (file) + optional PostgreSQL, React (Next.js or Vite)
