![status](https://img.shields.io/badge/status-in--development-yellow)

![kotlin](https://img.shields.io/badge/kotlin-language-7F52FF?logo=kotlin&logoColor=white)
![springboot](https://img.shields.io/badge/spring_boot-framework-6DB33F?logo=springboot&logoColor=white)
![postgresql](https://img.shields.io/badge/postgresql-database-316192?logo=postgresql&logoColor=white)

# InvestIQ - Backend

This document describes the backend module in this repository and how it is structured and intended to be used.

## Project layout
- **app-rest** - the HTTP REST API that serves the frontend and external clients. This is the primary API surface.
- **app-cli** - command-line utilities and batch tasks used for one-off jobs (imports, exports, ad-hoc analytics).
- **scheduler** - scheduled jobs (market data ingestion, nightly analytics recomputation, periodic cleanup).
- **common** - shared DTOs, utilities and configuration used by multiple backend subprojects.
- **service** - domain services and business logic modules used by app-rest and scheduler.
- **data** - SQL and PL/pgSQL scripts (functions and seed data) used to initialize and alter the database schema.

### Build & run
1. Prerequisites
    - JDK 21+
    - Docker (recommended for running PostgreSQL when developing)
    - From this repository the backend ships with a Gradle wrapper located in backend/ - use that to ensure consistent builds.

2. Run the REST API locally
```bash
  cd backend
```
- To run only the REST module (recommended for development):
```bash        
  ./gradlew :app-rest:bootRun
```
- To run the whole backend project (boots default project(s)):
```bash
    ./gradlew bootRun
```
- To build an executable jar:
```bash
    ./gradlew bootJar
```

3. Run CLI tasks
    - Use the app-cli project for import/export and maintenance commands:
    ```bash
        ./gradlew :app-cli:run
      ```

### Database & migrations
- This repo contains SQL/PLpgSQL scripts under backend/data/. Those scripts include:
    - schema DDL (tables, indexes)
    - plpgsql routines used by analytics, aggregations and scheduled calculations
    - seed/sample data used for local development
- Migrations are applied automatically by the app-rest module on startup using Flyway. In production, ensure that migrations are applied in a controlled manner during deployment.
- To run migrations manually, you can use the Flyway CLI or a database client to execute

### API and OpenAPI
- Look for API docs endpoints when running the backend (/v3/api-docs or /swagger-ui.html).

## What the backend does (features implemented in this repo)
- CRUD and query endpoints for portfolio entities:
    - portfolios, accounts, holdings, transactions
- Market-data ingestion pipeline (scheduler):
    - scheduled fetches of price data and metadata
    - normalization and storage of time-series used by analytics
- Analytics & aggregation services:
    - time-series performance (cumulative returns)
    - allocation breakdowns (by asset class, sector, currency)

## Tests & quality
- Unit tests: 
```bash
    ./gradlew test
```