# UI Flow

## Screens / views

| Screen | Purpose | Entry |
|--------|---------|-------|
| Ticket list | Search, filter, create, open detail | `/` |
| Ticket detail | View fields, edit, transition, comments | `/tickets/[id]` |

## Flows

### Create ticket

1. User opens list → “New ticket”
2. Fills title, description, priority, optional assignee
3. Submit → POST create → redirect/focus new ticket or refresh list
4. On 400 → show field errors

### List / search / filter

1. Optional keyword box (`q`)
2. Status dropdown (All + each status)
3. Results table: title, status, priority, assignee, updated

### View / update

1. Open detail
2. Edit title/description/priority/assignee → PATCH
3. Transition via buttons showing only **allowed** next statuses (UI hint); backend still enforces
4. Invalid transition (if forced) → show API error message

### Comments

1. On detail, list existing comments
2. Author + body form → POST comment → append to list

## Empty / error / loading

- Loading: spinner/skeleton on list and detail
- Empty: “No tickets match” on list
- Error: toast or inline alert with `message` + per-field errors from API

## Notes

API-only consumers are supported; this UI is the primary assessment surface.
