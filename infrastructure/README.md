# InvestIQ Docker Compose

Production stack that builds runnable JAR files for app-rest (8080) and scheduler (8081) with nginx-served Angular build (4200). PostgreSQL 18 on port 8432.

## Prerequisites
- Docker + Docker Compose V2
- Ports 8432, 8080, 8081, 4200 free on host

## Quick start
```bash
cd infrastructure
docker compose --env-file .env up --build
```

## Services
- **PostgreSQL**: `localhost:8432` (db: `investiq_db`, user: `postgres`, password: `postgres`) — configured via `.env`
- **app-rest**: `http://localhost:8080` — REST API with Swagger UI at `/swagger-ui.html`
- **scheduler**: `http://localhost:8081` — Background job scheduler
- **frontend**: `http://localhost:4200` — Angular SPA served by nginx (production build)

## Dockerfiles
- `infrastructure/backend/Dockerfile.app-rest` — Gradle build (gradle:9.2.1-jdk25-ubi10) → Eclipse Temurin 25 JRE
- `infrastructure/backend/Dockerfile.scheduler` — Gradle build (gradle:9.2.1-jdk25-ubi10) → Eclipse Temurin 25 JRE
- `infrastructure/frontend/Dockerfile` — Node.js 22 build → nginx:1.27-alpine (SPA routing, asset caching)

## Build Context
All builds use repo root (`..`) as build context. Dockerfiles copy `backend/` and `frontend/` directories into the build image.

## Volumes
- `investiq-pg-data` — PostgreSQL data persistence

## Common commands
```bash
# Stop and remove containers, keep volumes
docker compose down

# Rebuild all images (no cache)
docker compose build --no-cache

# View logs
docker compose logs -f app-rest scheduler frontend investiq-postgres

# Build individual services
docker compose build app-rest
docker compose build scheduler
docker compose build frontend
```
