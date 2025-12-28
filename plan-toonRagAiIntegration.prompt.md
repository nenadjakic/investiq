## Plan: TOON + RAG + AI tools (OpenAI + pgvector, sync)

Kratko: Implementirati TOON grammar i parser/serializer u `backend/common`, izraditi AI "toolove" (OpenAI client, embeddings), RAG pipeline (Postgres+pgvector) i memoriju (short-term + long-term), te expose sync REST endpoint `GET /portfolio/insights/ai?format=toon|json&model=...` koji vraća `PortfolioAiAnalysisResponse`. TOON se koristi za minimalan token usage pri razmjeni s modelom; postojeći DTO-i i poslovna logika ostaju, ali dodajemo TOON konverzore u `common`.

Checklist (kratko)
- [ ] Nacrtati TOON BNF i spremiti `toon-spec.md` u repo root.
- [ ] Dodati TOON konvertere i knjižnicu u `backend/common` (`ToonSerializer`, `ToonParser`, `ToonUtils`).
- [ ] Dodati AI tool interfaces i `OpenAiClient` u `backend/service`.
- [ ] Implementirati `PgVectorStore` i Flyway migration SQL u `backend/data`.
- [ ] Implementirati `RagIngestService`, `RagRetriever` i `MemoryService`.
- [ ] Implementirati `AiAnalysisService` koji orkestrira RAG + TOON + OpenAI pozive.
- [ ] Dodati REST endpoint u `backend/app-rest` i omogućiti `format=toon|json`.
- [ ] Tests: `ToonSerializerTest`, `AiAnalysisServiceTest`, `PortfolioControllerAiTest`.
- [ ] Docs: `toon-spec.md`, README snippets, docker-compose fragment za Postgres+pgvector.

Detaljan plan i file-level izmjene

1) `toon-spec.md` (repo root)
- Sadržaj: puna BNF grammar, mapa skraćenih ključeva (short keys), escape pravila, primjeri input/output za holdings, allocation i AI output (summary, flagged, suggestions).
- Predloženi BNF (line-oriented):
  - Document := Record ( ";" Record )*
  - Record := Field ( "|" Field )*
  - Field := Key "=" Value
  - Key := shortAlpha (1-3 letters)
  - Value := Unescaped | Escaped
  - Escaping: backslash `\` escapes `|`, `;`, `=` i `\\`
- Predloženi ključni mapovi (skraćenice):
  - holdings: `t`=ticker, `n`=name, `s`=shares, `ap`=avgPrice, `cp`=currentPrice, `mv`=marketValue, `pl`=profitLossEur, `plp`=profitLossPct, `pp`=portfolioPct, `ty`=type
  - allocation: `k`=key (country/sector), `v`=value (EUR or %)
  - ai-output: `S`=summary, `F` prefix for flagged assets (`F|t=...|sev=H|r=...;`), `G` prefix for guidance/suggestions (`G|action=SELL|t=...|amt=...|pct=...|r=...;`)

2) `backend/common` additions
- Create package: `com.github.nenadjakic.investiq.common.toon`
- Files to add:
  - `ToonSerializer.kt` — functions to convert DTOs to TOON strings, extension methods like `AssetHoldingResponse.toToonLine()` and deterministic ordering.
  - `ToonParser.kt` — parse TOON strings into DTOs (robust to missing fields and escaping).
  - `ToonUtils.kt` — escaping/unescaping helpers, key maps, small helpers to format numbers.
- Place DTOs (if missing) under `backend/common/src/main/kotlin/com/github/nenadjakic/investiq/common/dto/`:
  - `PortfolioAiAnalysisResponse.kt` (summary, flaggedAssets, suggestions, confidence, rawModelOutput)
  - `AiFlaggedAsset.kt` (ticker, reason, severity, metrics map)
  - `AiRebalanceSuggestion.kt` (action, ticker, amountEur, percentageOfPortfolio, rationale)
  - `AiMemoryEntry.kt` (id, userId, text, metadata, createdAt)
- Tests: `ToonSerializerTest.kt` for round-trip serialization.

3) `backend/service` — AI tools & OpenAI client
- Add package: `com.github.nenadjakic.investiq.service.ai`
- Files to add:
  - `AiClient.kt` (interface): `fun generate(prompt: String, model: String, opts: AiOptions): AiResponse`
  - `OpenAiClient.kt` (impl): HTTP client to OpenAI, read API key from `OPENAI_API_KEY` or Spring property `spring.ai.api-key`; support configurable model param and basic retry/backoff.
  - `EmbeddingTool.kt` (interface) + `OpenAiEmbeddingTool.kt` (impl): `fun embed(text: String, model: String): FloatArray`.
  - `AiOptions.kt` and `AiResponse.kt` small DTOs.
- Add lightweight safety/size checks in `OpenAiClient` to guard prompt size and disallow disallowed models by config.
- Config: add `backend/service/src/main/resources/application.yml` snippets for ai.props (allowed models, default model, embedding model).

4) `backend/data` — DB migration and pgvector schema
- Add Flyway migration SQL file: `backend/data/src/main/resources/db/migration/V1__create_ai_vectors.sql`
- SQL should create tables (Postgres + pgvector extension required):
  - `ai_vectors` (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source TEXT,
    metadata JSONB,
    toon_snippet TEXT,
    embedding VECTOR(1536),
    created_at TIMESTAMPTZ DEFAULT now()
  );
  - `ai_memory` (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT,
    text TEXT,
    metadata JSONB,
    embedding VECTOR(1536),
    created_at TIMESTAMPTZ DEFAULT now()
  );
- Document in README how to enable `pgvector` extension (CREATE EXTENSION IF NOT EXISTS vector;) and example `docker-compose` fragment.

5) `backend/service` — Vector store & RAG pipeline
- Add package: `com.github.nenadjakic.investiq.service.vector`
- Files:
  - `PgVectorStore.kt` — uses `JdbcTemplate` for upsert and similarity search
    - Methods: `upsert(record: VectorRecord)`, `search(queryEmbedding: FloatArray, topK: Int): List<VectorRecord>`
  - `VectorRecord.kt` — data class (id, source, metadata, toonSnippet, embedding, createdAt)
- Add `RagIngestService.kt` in `rag` package — chunking policy, create TOON snippet per chunk using `ToonSerializer`, get embedding via `EmbeddingTool`, upsert to `PgVectorStore`.
- Add `RagRetriever.kt` — search topK and return `VectorRecord` including `toon_snippet`.

6) `backend/service` — MemoryService
- Add `MemoryService.kt` in `memory` package
  - Short-term: `ConcurrentHashMap<String, LinkedList<AiMemoryEntry>>` keyed by sessionId with TTL eviction (use `ScheduledExecutorService` or Spring Cache with TTL).
  - Long-term: persist to `ai_memory` via `PgVectorStore` (or separate DAO) with embeddings.
  - Methods: `saveInteraction(userId, inputToon, responseToon)`, `queryMemory(userId, query, topK)`.

7) `backend/service` — Orchestrator `AiAnalysisService`
- File: `AiAnalysisService.kt`
- Inject: `PortfolioService`, `RagRetriever`, `ToonSerializer`, `AiClient`, `MemoryService`.
- Flow:
  1. `holdings = portfolioService.getPortfolioHoldings()`; `allocation = portfolioService.getAllocation()`
  2. `portfolioToon = ToonSerializer.toToon(holdings, allocation)`
  3. `contexts = ragRetriever.retrieve(query, topK)` (prefer `toon_snippet`)
  4. Build prompt: system instruction (TOON BNF + role), append contexts (TOON), append `portfolioToon`, append instruction to return TOON output per `toon-spec`.
  5. Call `aiClient.generate(prompt, model, opts)` (model optional param default from config)
  6. Parse output with `ToonParser` into `PortfolioAiAnalysisResponse`.
  7. Save in `MemoryService.saveInteraction(userId, portfolioToon, modelOutput)`.
- Expose method `analyzePortfolio(model: String?, format: String, strategy: String?): PortfolioAiAnalysisResponse`

8) `backend/app-rest` — Controller changes
- Edit `PortfolioController.kt` to add endpoint:
  - `@GetMapping("/insights/ai") fun getAiInsights(@RequestParam(required=false) model: String?, @RequestParam(required=false, defaultValue="toon") format: String, @RequestParam(required=false) strategy: String?): ResponseEntity<Any>`
  - Inject `AiAnalysisService`; call `analyzePortfolio(model, format, strategy)`
  - If `format=="json"` return parsed DTO (JSON). If `format=="toon"` return `text/plain` with TOON output (raw model TOON or serialized DTO depending on desired transparency).
- Add optional `format` support to `GET /portfolio/holdings` and `GET /portfolio/allocation` to return TOON for clients that want compact payloads.

9) Tests & CI
- Tests to add in appropriate `src/test` locations (mocking with Mockito/Kotlin, use JUnit 5):
  - `ToonSerializerTest.kt` — round-trip and escape handling
  - `PgVectorStoreTest.kt` — integration test requires testcontainer Postgres with pgvector OR mock JdbcTemplate
  - `AiAnalysisServiceTest.kt` — mock `PortfolioService`, `RagRetriever`, `AiClient`
  - `PortfolioControllerAiTest.kt` — controller tests with `MockMvc` for `format=toon|json`

10) Docs & infra
- `toon-spec.md` in repo root with full grammar and examples
- `backend/README.md` updated with env vars and Quickstart:
  - `OPENAI_API_KEY`
  - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
  - `ALLOWED_AI_MODELS` (CSV of allowed models)
- Add `infrastructure/postgres-pgvector-compose.yaml` or append to `infrastructure/docker-compose.yaml` the Postgres service with `pgvector` install instructions and extension creation step.

Defaults & recommendations
- Default generation models (configurable): `gpt-4o-mini` (default for cost), `gpt-4o`, `gpt-4` (if available). Embeddings: `text-embedding-3-small`.
- Short-term memory TTL: 4 hours (configurable)
- RAG retrieval: topK=6 by default; chunk size ~800 tokens; adjust after token-count experiments.
- Use Flyway for DB migrations (project already has Gradle modules; add Flyway to `backend/data` if not present).
- Rate-limit endpoint and cache identical requests for 1h to save cost.

Next steps I can implement immediately
- Create `toon-spec.md` with full BNF + examples.
- Add `ToonSerializer`/`ToonParser` skeletons in `backend/common` plus DTOs.
- Add `OpenAiClient` skeleton and `EmbeddingTool` in `backend/service`.
- Create Flyway migration SQL in `backend/data`.

If you confirm, I will create the `toon-spec.md` and the file-level skeletons next. I will proceed with the implementation files after you confirm.

