# Architecture

This repo is structured as a small, interview-friendly, “clean-ish” multi-module Android app.

## Module Graph (Dependency Direction)

Rule of thumb: dependencies flow **inward** (UI → domain), and implementations live **outward** (data → domain).

Allowed dependencies:

- `:core` → *(no app modules)*  
  Shared UI primitives (Compose theme, shared components, shared strings).
- `:domain` → *(no app modules)*  
  Pure-ish business layer: models, repository interfaces, use cases, and DI qualifiers.
- `:data` → `:domain`  
  Implementations: Retrofit/Room, repository implementations, and Hilt bindings.
- `:feature:*` → `:core`, `:domain`  
  UI + ViewModel for a single feature. Should depend on `:data` **only** if truly necessary; prefer `:domain` interfaces.
- `:app` → `:core`, `:domain`, `:data`, `:feature:*`  
  Composition root: navigation graph, app manifest, `@HiltAndroidApp`, and app-only configuration.

Notes:

- App configuration values (like GitHub org/base URL) are provided via Hilt in `:app` and injected into `:data`/features using qualifiers defined in `:domain`.
- Shared resource names can collide across feature modules; keep truly shared strings in `:core` and feature-specific strings in the feature module.

## Where Things Live

- Domain contracts + use cases: `domain/src/main/java/com/example/sa/domain/...`
- Data implementations + DI modules: `data/src/main/java/com/example/sa/data/...`
- Feature UI + state + VM: `feature/<name>/src/main/java/com/example/sa/feature/<name>/...`
- Shared Compose theme + components: `core/src/main/java/com/example/sa/core/ui/...`
- App navigation host + wiring: `app/src/main/java/com/example/sa/...`

## Adding A New Feature Module

Example: add `:feature:search` (replace `search` with your feature name).

1. Create the module directory:
   - `feature/search/build.gradle.kts`
   - `feature/search/src/main/AndroidManifest.xml` (empty manifest)
   - `feature/search/src/main/java/com/example/sa/feature/search/...`

2. Add the module to `settings.gradle.kts`:
   - `include(":feature:search")`

3. Set up dependencies (typical):
   - `implementation(project(":core"))`
   - `implementation(project(":domain"))`
   - Add Compose + lifecycle + Hilt (and `ksp(libs.hilt.compiler)`) if you have a `@HiltViewModel`.

4. Expose an entry point:
   - Provide a composable screen (e.g. `SearchScreen(...)`) and a `@HiltViewModel`.
   - Keep navigation routes centralized in `:app` (so the app owns the graph).

5. Wire navigation in `:app`:
   - Add a route constant + argument(s) (if applicable).
   - Add a `composable(...) { SearchScreen(...) }` destination.

Keeping features isolated like this makes it easy to add/remove features during interviews without touching core business logic.

