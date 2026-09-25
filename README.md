# Support Ticket Management System

Spec-driven Support Ticket Management System built with Java 21, Spring Boot, H2/PostgreSQL, and React (Vite).

## Specs

See [`spec/`](spec/) for requirements, architecture, API contract, state machine, UI flow, and test strategy.

## Backend

```bash
export JAVA_HOME="$(pwd)/.tools/jdk-21.0.8+9"   # or your JDK 21
export PATH="$JAVA_HOME/bin:$PATH"
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080/api/v1/tickets`

- Default DB: H2 file `backend/data/tickets` (survives restart)
- PostgreSQL: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres` with env `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` (never commit secrets)

```bash
cd backend && mvn verify   # unit (*Test) + integration (*IT)
```

## Frontend

```bash
cd frontend
npm install
npm run dev
```

UI: `http://localhost:5173` (proxies `/api` to the backend)

## AI workflow artefacts

- `.cursor/rules/` — Java/Spring, testing, API, spec-first, prompt history
- `.cursor/skills/documentation/`
- `.cursor/commands/` — review-code, review-spec, generate-tests
- `.specstory/history/` — SpecStory chat exports
- `docs/prompt-history.md` — prompt index + AI mistake reviews
