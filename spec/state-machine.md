# State Machine

## Statuses

| Status | Meaning |
|--------|---------|
| OPEN | Newly created; not yet worked |
| IN_PROGRESS | Actively being handled |
| RESOLVED | Fix proposed / work done; awaiting close |
| CLOSED | Terminal success |
| CANCELLED | Terminal cancelled |

## Allowed transitions

| From | To | Trigger |
|------|----|---------|
| OPEN | IN_PROGRESS | Start work |
| OPEN | CANCELLED | Cancel |
| IN_PROGRESS | RESOLVED | Mark resolved |
| IN_PROGRESS | CANCELLED | Cancel |
| RESOLVED | CLOSED | Close |

## Explicitly forbidden (examples)

| From | To | Allowed? |
|------|----|----------|
| CLOSED | OPEN | No |
| RESOLVED | OPEN | No |
| CANCELLED | OPEN | No |
| CLOSED | * | No (terminal) |
| CANCELLED | * | No (terminal) |
| OPEN | RESOLVED | No |
| OPEN | CLOSED | No |
| IN_PROGRESS | CLOSED | No (must go via RESOLVED) |
| RESOLVED | CANCELLED | No |
| RESOLVED | IN_PROGRESS | No |

## Enforcement

- Backend only: `TicketStatusTransitionService` holds the allow-list
- Illegal transition → `400` with message like `Invalid status transition: CLOSED → OPEN`
- Create always starts at `OPEN`
- PATCH must not accept `status` (prevents bypass)

## Notes

Same-status transition (`OPEN → OPEN`) is rejected.
