![status](https://img.shields.io/badge/status-in--development-yellow)

![kotlin](https://img.shields.io/badge/kotlin-language-7F52FF?logo=kotlin&logoColor=white)
![angular](https://img.shields.io/badge/angular-framework-DD0031?logo=angular&logoColor=white)
![springboot](https://img.shields.io/badge/spring_boot-framework-6DB33F?logo=springboot&logoColor=white)
![docker](https://img.shields.io/badge/docker-container-blue?logo=docker&logoColor=white)
![postgresql](https://img.shields.io/badge/postgresql-database-316192?logo=postgresql&logoColor=white)

# InvestIQ

InvestIQ is an investment portfolio management platform that helps investors and advisors track multi-asset portfolios, understand performance and risk.
This repository contains the application backend (Kotlin/Spring Boot), the frontend (Angular/TypeScript) and the infrastructure artifacts used to run the system locally or in production.
This README is the first place a user or contributor sees - it explains what the product *does*, what users will see in the UI, and how the pieces fit together so interested people continue to the module READMEs.

## About
- For busy investors and small advisory teams who need clear, data-driven portfolio oversight, InvestIQ aggregates positions and transactions across accounts, computes portfolio-level analytics (returns, allocation, risk), visualizes them with interactive charts.

### Tech stack
- **Backend**: Kotlin, Spring Boot, Spring Data JPA, Spring Scheduler, Flyway, Gradle
- **Frontend**: TypeScript, Angular, tailwind, ngx-echarts, npm
- **Infrastructure**: Docker, docker-compose
- **API contract**: OpenAPI

### Who this is for
- Individual investors who want better visibility into their allocations and performance.
- Developers and data engineers who want to extend analytics, add new data sources, or add integrations with broker/exchange APIs.

### What InvestIQ does (concise feature list)
- Ingests and normalizes market price/time-series and transaction data.
- Tracks portfolios, accounts, holdings and transactions across assets.
- Computes performance, cumulative returns, drawdown, volatility and allocation breakdowns.
- Displays interactive, exportable charts and tables in the frontend (ECharts via ngx-echarts).
- Runs scheduled ingestion and recomputation jobs.

### First impression - what users will see in the UI (so it hooks them)
- A modern dashboard with:
    - Portfolio summary: total current market value, change for different ranges
    - Allocation visualization: donut/pie showing asset-class and currency allocation
    - Performance chart: interactive time-series with overlays, zoom and tooltips
    - Holdings table: sortable list with market value, unrealized P/L, allocation %
    - Per-asset detail panel: price history, transactions, position analytics
- The UI is built using Angular and Tailwind to keep a professional look.

### How the pieces fit together (simple architecture)
- Frontend (Angular SPA)
    - UI, charts, API client generated from OpenAPI
- Backend (Kotlin, Spring Boot)
    - app-rest: REST API & OpenAPI spec
    - scheduler: periodic ingestion and recalculation jobs
    - app-cli: batch / import
    - agent: lightweight standalone AI agent/service that performs specialized tasks and can run independently or in container
    - common / service: shared domain logic and analytics
- Data
    - PostgreSQL for normalized time-series, transactions and derived analytics
    - PL/pgSQL routines live in backend/data and top-level data/ for analytics performance
- Infrastructure
    - docker-compose

### Quick demo flow (what you can do in 10 minutes)
1. Start a local Postgres (or use docker-compose).
2. Apply schema scripts found in backend/data (see backend README).
3. Start backend: ./gradlew :app-rest:bootRun (from `backend/`) - backend exposes API + OpenAPI. app-rest will apply migrations automatically on startup.
4. Generate frontend client: cd frontend && npm run generate:api
5. Start frontend: npm start and open http://localhost:4200
6. Import a sample transactions CSV (or use seed data), open Dashboard.

### Repository layout (helpful pointers)
- backend/
    - agent/ - lightweight AI agent service — standalone Spring Boot module; see `backend/agent/README.md`
    - app-rest/ - HTTP API (controllers, OpenAPI)
    - app-cli/ - CLI utilities (import CSV data)
    - scheduler/ - scheduled ingestion & recompute jobs
    - common/, service/ - domain logic
    - data/ - SQL, PL/pgSQL scripts and sample data
    - build.gradle.kts, gradlew - Gradle wrapper present under backend/
- frontend/
    - Angular 21 app
    - package.json includes: "generate:api" script that uses openapi-generator-cli and ngx-echarts and Angular Material
    - key files: frontend/src/dashboard, frontend/src/custom-theme.scss, frontend/openapi.json (if present)
- infrastructure/
    - docker-compose.yml, k8s manifests or CI helpers (inspect directory to see which apply)
- data/ - additional SQL / sample files used across modules

## Getting started (developer quick commands)
- **Backend**
```bash
cd backend
./gradlew :app-rest:bootRun
```
- **Frontend**
```bash
cd frontend
npm ci
npm start
```

### Outputs & reports
- Backend artifacts (jars): each module produces build artifacts under `backend/<module>/build/libs/` and there is an aggregated `backend/build/libs/` for multi-module artifacts.
- Test reports and coverage: unit test reports and jacoco coverage are generated at `backend/<module>/build/reports/tests/` and `backend/<module>/build/reports/jacoco/` respectively. A quick way to generate reports for all backend modules:
```bash
cd backend
./gradlew test jacocoTestReport
```
- Frontend production build: `frontend/dist/` (used by `infrastructure` Docker image / nginx)

### Important repo-specific notes
- OpenAPI-driven workflow: update backend controllers/DTOs → ensure OpenAPI spec is published by app-rest → run frontend/npm run generate:api to regenerate typed client.
- SQL and PL/pgSQL scripts are tracked under backend/data and top-level data/ - treat these scripts as versioned artifacts and apply them via a migration process in staging/production.
- Gradle wrapper is available in backend/ so use ./gradlew for consistent builds.

### Contributing & code quality
- Branch: feature/<name> or fix/<issue>
- PRs: explain the feature, list breaking changes, include screenshots for UI changes
- Tests: run backend unit tests with ./gradlew test and frontend tests with npm test
- Linters: use ktlint / detekt for backend and ESLint / Prettier for frontend (Prettier config in package.json)

## Links (quick)
- backend README: ./backend/README.md
- frontend README: ./frontend/README.md
- infrastructure README: ./infrastructure/README.md
- Repo: https://github.com/nenadjakic/investiq

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.