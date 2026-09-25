# Test Strategy

## Approach

Follow `.cursor/rules/testing.mdc`: JUnit 5, Mockito, `*Test` / `*IT`, right Spring slice.

## Coverage plan

| Area | Type | Examples |
|------|------|----------|
| Transition allow-list | Unit `TicketStatusTransitionServiceTest` | Every allowed edge; representative illegal edges |
| TicketService | Unit | Create defaults; update fields; reject bad transition via collaborator |
| Validation / HTTP | `@WebMvcTest` or `@SpringBootTest` | 400 on blank title; 404 unknown id |
| State machine + DB | `*IT` `@SpringBootTest` | Persist ticket; walk OPEN→…→CLOSED; assert CLOSED→OPEN → 400; restart not required in-process but file H2 used |

## Acceptance criteria → tests

| Criterion | Test |
|-----------|------|
| Valid transitions work | IT: OPEN→IN_PROGRESS→RESOLVED→CLOSED; OPEN→CANCELLED; IN_PROGRESS→CANCELLED |
| Invalid transitions rejected | IT: CLOSED→OPEN, RESOLVED→OPEN, CANCELLED→OPEN return 400 |
| Validation | Web/IT: blank title → 400 with fieldErrors |
| Search / filter | IT or repository test with sample data |
| Comments | IT: add comment appears on detail |

## Out of scope for automated tests

- Full Playwright E2E (manual UI check acceptable for assessment)
- PostgreSQL-specific tests (H2 file profile for CI/local)
