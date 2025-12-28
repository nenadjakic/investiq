## Plan: Integracija Spring AI za analizu portfelja

Kratko: Dodati backend AI sloj koji preuzima postojeće podatke o portfelju (holdings, alokacije) i šalje ih LLM-u preko najnovije Spring AI biblioteke; LLM vraća sažetak stanja, označene (loše/jake) pozicije i prijedloge preraspodjele. Razlog: brzo dobivanje razumljivih, tekstualnih preporuka bez mijenjanja postojećih repozitorija podataka.

### Steps
1. Dodati Spring AI dependency u `backend/service/build.gradle.kts` (ili u `gradle/libs.versions.toml`) i konfigurirati provider key putem env var (npr. `OPENAI_API_KEY`) / Spring property.
2. Napraviti DTO u `backend/common/src/main/kotlin/com/github/nenadjakic/investiq/common/dto/PortfolioAiAnalysisResponse.kt` (`PortfolioAiAnalysisResponse`, `AiFlaggedAsset`, `AiRebalanceSuggestion`).
3. Implementirati servis `AiAnalysisService` u `backend/service/src/main/kotlin/com/github/nenadjakic/investiq/service/AiAnalysisService.kt` koji:
   - poziva `PortfolioService.getPortfolioHoldings()` i/ili `PortfolioController`-om dostupne metode (`PortfolioService.getAllocation()` itd.),
   - gradi strukturirani prompt (holdings + alokacije + ciljevi/parametri),
   - poziva Spring AI client (konfigurirani provider) i parsira rezultat u `PortfolioAiAnalysisResponse`.
4. Dodati REST endpoint u `backend/app-rest/src/main/kotlin/.../controller/PortfolioController.kt`:
   - npr. `@GetMapping("/insights/ai") fun getAiInsights(@RequestParam(required=false) strategy:String?): ResponseEntity<PortfolioAiAnalysisResponse>` koji injektira i poziva `AiAnalysisService`.
5. Dodati unit testove:
   - `backend/service/src/test/kotlin/.../AiAnalysisServiceTest.kt` — mokati `PortfolioService` i Spring AI client; assert strukturiran odgovor.
   - `backend/app-rest/src/test/.../PortfolioControllerAiTest.kt` — integracijski test endpointa (mokovi).
6. Dokumentacija + konfig:
   - ažurirati `backend/README.md` ili glavni `README.md` s uputama: kako postaviti API key env var, koji provider/model je preporučen, troškovi i ograničenja tokena.
   - dodati osnovne properties primjere (`backend/service/src/main/resources/application.yml` ili README snippet).

### Further Considerations
1. Provider + model: želite li OpenAI (gpt-4o/4o-mini), Azure OpenAI, ili drugi? (Preporuka: OpenAI/llama-2/Anthropic kao opcije.)
2. Sync vs Async: želite li trenutni REST sync odgovor (jednostavno, ali može probiti timeout) ili asinkronu obradu (zadaci + notifikacija)? Option A: sync za MVP / Option B: async za veće portfelje.
3. Sigurnost i privatnost: držati API key izvan repoa (env var), rate-limit i cache odgovor (ponavljanja često ista analiza).

Napomena: ovo je nacrt plana — prije implementacije mogu pripremiti konkretnu listu točnih gradle dependency koordinata i primjer prompta (stručno s obrascem ulaza/izlaza) ako potvrdite provider/model i želite sync ili async endpoint.

