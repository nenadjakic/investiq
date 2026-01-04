# backend/agent - AI Agent module

Purpose
- The `agent` module is a lightweight, standalone Spring Boot application that performs specialized background tasks (data collection, event forwarding or AI-assisted features). It can run independently, in Docker, or as part of the composed infrastructure.

How to build
```bash
# from repository root
./gradlew :backend:agent:build
# or from backend/ folder
cd backend
./gradlew :agent:build
```

How to run
```bash
# Run with Gradle (development)
./gradlew :backend:agent:bootRun

# Run the produced jar (production-like)
java -jar backend/agent/build/libs/agent-<version>.jar
```

Outputs & reports
- Built jars: `backend/agent/build/libs/`
- Test reports: `backend/agent/build/reports/tests/`
- Coverage: `backend/agent/build/reports/jacoco/`

Notes
- Environment variables and runtime configuration (ports, DB connection, API keys) are read from the usual Spring configuration locations (application.yml, environment variables). Ensure `AGENT_PORT`, `SPRING_DATASOURCE_URL` or other required env vars are set when running in production.
- Docker: the `infrastructure` compose file includes an `agent` service and the Dockerfile is located at `infrastructure/backend/Dockerfile.agent`.
- If the agent communicates with other services (app-rest, scheduler, database), ensure proper network configuration when running with Docker Compose.

