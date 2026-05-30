# AGENTS — Quick reference for AI coding agents

Purpose: give an AI coding agent the essential, discoverable knowledge to be immediately productive in this repo (what to run, where to change code, and concrete examples).

1) Big picture (what touches what)
- Frontend (Angular 21) — single-page app in `frontend/`. Uses OpenAPI-generated TypeScript Angular clients under `src/app/*/api`.
- Backend (Kotlin + Spring Boot) — multi-module Gradle project under `backend/` with modules: `data`, `common`, `service`, `integration`, `app-cli`, `app-rest`, `scheduler`, `toon`, `agent`. See `backend/settings.gradle.kts`.
- Data / runtime — PostgreSQL stores normalized time-series, transactions and derived analytics. Flyway + SQL scripts live under `backend/data` and top-level `data/`.
- Infrastructure — `infrastructure/docker-compose.yaml` and Dockerfiles under `infrastructure/backend/` and `infrastructure/frontend/` build & run the system.

2) Primary developer workflows (concrete commands)
- Backend (dev):
  - cd into `backend/` and use the Gradle wrapper there. Typical dev run:
    ```bash
    cd backend
    ./gradlew :app-rest:bootRun
    ```
  - Run other modules similarly (e.g. `:agent:bootRun`, `:scheduler:bootRun`, `:app-cli:run`).
- Backend (build/tests/coverage):
    ```bash
    cd backend
    ./gradlew test jacocoTestReport
    ```
  - Test reports and coverage: `backend/<module>/build/reports/tests/` and `backend/<module>/build/reports/jacoco/`.
- Frontend (dev):
    ```bash
    cd frontend
    npm ci
    npm start        # runs ng serve -> http://localhost:4200
    ```
- Frontend API client generation (exact commands from `package.json`):
    ```bash
    # regenerate typed Angular client for core API
    cd frontend
    npm run generate:core-api

    # regenerate typed Angular client for AI agent API (agent runs on 8082 by default)
    npm run generate:ai-api
    ```
  Note: these scripts use `del-cli` to remove the existing generated files and `@openapitools/openapi-generator-cli` with properties `fileNaming=kebab-case,ngVersion=20.0.0`.

3) OpenAPI / codegen flow (canonical steps)
- Run `app-rest` locally (`:app-rest:bootRun`) so the app serves `/v3/api-docs` (openapi JSON). The Gradle OpenAPI plugin in `backend/app-rest/build.gradle.kts` is configured to fetch `http://localhost:8080/v3/api-docs` and write to `build/openapi/openapi.json`.
- Regenerate frontend clients: run `npm run generate:core-api` / `generate:ai-api` after the API is available.

4) Docker / compose (how the composed system is wired)
- Top-level compose: `infrastructure/docker-compose.yaml` defines services: `postgres`, `app-rest`, `scheduler`, `agent`, `frontend`.
- Important env var names shown in compose: `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`, `POSTGRES_PORT`, `APP_REST_PORT` (default 8080), `SCHEDULER_PORT` (8081), `AGENT_PORT` (8082), `FRONTEND_PORT` (4200).
- Flyway migrations are applied by Spring Boot on startup (Flyway locations configured via `SPRING_FLYWAY_LOCATIONS`). SQL init files for Docker are under `./.postgres/init` (volume mount configured).

5) Conventions & quick pointers an agent should follow
- Module layout: mutate code only in the correct module: REST controllers → `backend/app-rest/src/main/kotlin`, domain/service logic → `backend/service` or `backend/common`.
- SQL & migrations: change SQL in `backend/data` and top-level `data/`. Do not edit runtime-generated DB schema in `build/`.
- Gradle wrapper: there is a wrapper in `backend/` — use `./gradlew` from `backend/` for Java/Kotlin tasks to ensure consistent toolchain.
- Kotlin/Java targets & group id: group is `com.github.nenadjakic.investiq` and Kotlin target jvm is 24 (see `backend/build.gradle.kts`).
- Frontend generation: generated API client files live under `frontend/src/app/core/api` and `frontend/src/app/ai/api` — do not hand-edit generated files; change server-side DTOs or generator options instead.

6) Integration points and places to look for examples
- OpenAPI spec endpoints: `app-rest` (http://localhost:8080/v3/api-docs) and `agent` (http://localhost:8082/v3/api-docs). See `frontend/openapi.core.json` and `frontend/openapi.ai.json` which are source-of-truth snapshots used for client generation.
- Dockerfiles: `infrastructure/backend/Dockerfile.app-rest`, `infrastructure/backend/Dockerfile.scheduler`, `infrastructure/backend/Dockerfile.agent` — useful when producing containerized images.
- Healthchecks and default ports: defined in `infrastructure/docker-compose.yaml` — helpful when orchestrating services in tests.

7) Debugging and quick discoverability
- To find Spring Boot entrypoints / controllers search `backend/**/src/main/kotlin` for `@SpringBootApplication` and `@RestController` annotations.
- Logs: `spring-shell.log` files appear in some modules (root/backend/) and Spring Boot logs go to standard output when running with `bootRun`.
- If Gradle task names are unknown, run `./gradlew :app-rest:tasks` (from `backend/`) to list tasks (including any openapi extraction tasks).

8) Small example tasks (explicit file pointers)
- Add REST endpoint: update controller in `backend/app-rest/src/main/kotlin/.../controller/` (search for `PortfolioController` or `TransactionController`), update DTOs in `backend/common` or `backend/service`, run `./gradlew :app-rest:bootRun`, then `npm run generate:core-api` in `frontend`.
- Fix DB migration: add/modify SQL in `backend/data/migrations` (or top-level `data/`), test by running `./gradlew :app-rest:bootRun` against a local Postgres or `docker compose up`.

9) Where not to waste time
- Do not edit generated OpenAPI clients under `frontend/src/app/*/api` unless you also change generation templates; changes will be overwritten by `npm run generate:*`.

10) Useful files & entry points (quick reference)
- `backend/settings.gradle.kts` — module list
- `backend/build.gradle.kts` — shared Kotlin/Gradle config
- `backend/app-rest/build.gradle.kts` — OpenAPI extraction + web starter
- `frontend/package.json` — client generation scripts
- `frontend/openapi.core.json`, `frontend/openapi.ai.json` — snapshots of API specs
- `infrastructure/docker-compose.yaml` — environment wiring and ports
- `backend/data/` and `data/` — SQL and PL/pgSQL scripts

If something appears missing, run these discovery commands from repository root:
```bash
cd backend && ./gradlew :app-rest:tasks
cd frontend && npm run generate:core-api --dry-run
grep -R "@RestController\|@SpringBootApplication" backend | sed -n '1,120p'
```

— End of AGENTS.md

