# Prompt History

This project records AI prompts so work is reviewable and not accepted blindly.

## How prompts are saved

| Mechanism | Location | Notes |
|-----------|----------|--------|
| **SpecStory** (Cursor / VS Code) | [`.specstory/history/`](../.specstory/history/) | Install the SpecStory extension; autosave is on by default. Use Command Palette → `SpecStory: Save AI Chat History` for manual export. |
| **Session index** | this file | Summaries, outcomes, and **AI mistake reviews** |

Kiro users: use a small skill that appends each prompt to `.specstory/history/` and updates this index. This repo is Cursor-first via SpecStory.

## Session: project bootstrap (Cursor rules, skills, commands, specs)

### Prompts (chronological)

| # | Prompt (summary) | Outcome |
|---|------------------|---------|
| 1 | `/create-rule` | Clarified purpose/scope |
| 2 | `java-springboot.md` / `testing.md` / `api-standards.md` | Created `.cursor/rules/*.mdc` |
| 3 | Implement plan (rules) | Wrote the three rule files |
| 4 | `/create-skill` | Asked location/purpose |
| 5 | `documentation/` then “go for 1” | Project skill at `.cursor/skills/documentation/` |
| 6 | `commands/` → `review-code.md`, `review-spec.md`, `generate-tests.md` | Created `.cursor/commands/*.md` |
| 7 | Spec-before-implementation + `spec/` tree | Rule + scaffold under `spec/` |
| 8 | Prompt history + SpecStory layout + mistake review | This doc + `.specstory/history/` + rule |

### Decisions

- Cursor rules use `.mdc` with file globs; slash commands stay plain `.md`.
- Specs live under `spec/`; must exist before non-trivial implementation.
- Prompt persistence is mandatory via SpecStory + this index.

---

## AI mistakes / incorrect suggestions (required review)

Engineering use of AI means **catching bad output**. Below are real issues from this bootstrap—not hypothetical.

### 1. Bad glob in `java-springboot.mdc` (incorrect suggestion)

**What the AI did:** First draft used `globs: "**/*.{java},..."`.

**Why wrong:** Brace expansion for a single extension is pointless and easy to mis-copy; the plan already specified `**/*.java`.

**Correction:** Changed to `**/*.java,**/pom.xml,**/build.gradle*,**/application*.{yml,yaml,properties}`.

### 2. Conflicting doc locations (incorrect architecture)

**What the AI did:** Documentation skill tells the agent to write architecture/API docs under `docs/architecture.md` and `docs/api.md`. Later, spec-first work put the canonical design under `spec/architecture.md` and `spec/api-contract.md`.

**Why wrong:** Two parallel homes for the same content invite drift and duplicate updates.

**Correction applied:** Updated `.cursor/skills/documentation/SKILL.md` so `spec/` is the design source of truth; `docs/` stays for indexes (e.g. this file); README links to `spec/architecture.md` and `spec/api-contract.md`.

### 3. Empty `spec/` scaffolds can look “done” (process risk)

**What the AI did:** Created full placeholder tables under `spec/*.md` immediately.

**Why risky:** The always-on “spec before implementation” rule could be satisfied by empty stubs while acceptance criteria and API contracts are still undefined—implementation would then proceed against fiction.

**Correction:** Before coding a feature, replace placeholders with real requirements/contracts (or explicitly mark sections N/A with a reason). Do not treat scaffold checkboxes as acceptance criteria.

### 4. Assumed SpecStory was already writing history (tooling gap)

**What could go wrong:** Relying only on `.specstory/history/` without installing SpecStory leaves an empty folder and no real prompt trail.

**Correction:** Install SpecStory in Cursor, confirm files appear after a chat, and keep updating this index with mistake reviews for submission/audit quality.

---

## Session: Support Ticket implementation — 2026-09-24

### Prompts

- Build Support Ticket Management System (Java 21, Spring Boot, H2/Postgres, REST, React, Cursor) with acceptance criteria and state machine from assessment doc.

### Outcome

- Filled `spec/*` then implemented `backend/` + `frontend/`
- State-machine unit + IT tests passing (`mvn test`)
- README, `.gitignore`, `.env.example` (no secrets committed)

### AI mistakes / pushback

#### 5. Invalid YAML disguised as properties

**Mistake:** First `application.yml` used `spring.datasource.url=...` flat properties syntax inside a `.yml` file.

**Why wrong:** YAML does not treat that as nested Spring keys the same way `.properties` does; config can silently fail or mis-bind.

**Fix:** Rewrote as proper nested YAML under `spring.datasource` / `spring.jpa`.

#### 6. Environment only had Java 11/17

**Mistake risk:** Generating a Java 21 project and assuming the machine JDK matched would break builds (`release version 21 not supported`).

**What we did:** Downloaded Temurin 21 into `.tools/` (gitignored) and ran Maven with that `JAVA_HOME`. Documented in README.

#### 8. `*IT` classes not run by Surefire

**Mistake:** Named state-machine tests `TicketStateMachineIT` and assumed `mvn test` executed them. Surefire only runs `*Test` by default; Failsafe runs `*IT`.

**Why wrong:** Green `mvn test` (15 unit tests) hid missing integration coverage for the core acceptance criterion.

**Fix:** Added `maven-failsafe-plugin` and run `mvn verify` so `*IT` tests execute.
