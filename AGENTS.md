# Repository Guidelines

## Project Structure & Module Organization
This repository is a small Clojure library built with `tools.deps`. Production code lives under `src/ollama/cli/` and is split by concern: `chat.clj` wraps the Ollama API, `config.clj` manages runtime configuration, `core.clj` contains shared helpers, and `schemas.clj` defines request/response specs. Tests mirror that structure under `test/ollama/cli/`, with a lightweight local test runner in `test/ollama/cli/test_runner.clj`. Default settings are stored in `resources/config.edn`, and longer-form notes belong in `doc/`.

## Build, Test, and Development Commands
Use the Clojure CLI for local work:

- `clojure -M:test` runs the full test suite in `test/`.
- `clojure -M:test` is the only built-in test command today; add a narrower runner option later if namespace-scoped execution becomes necessary.

Install the Clojure CLI and a compatible JDK before contributing. There is no separate build pipeline in this repo today; tests are the main verification step.

## Coding Style & Naming Conventions
Follow standard Clojure formatting: two-space indentation, aligned bindings where it improves readability, and one namespace per file. Keep namespaces consistent with paths, for example `src/ollama/cli/chat.clj` -> `ollama.cli.chat`. Prefer kebab-case for vars and functions (`reset-config!`, `stream-payload`) and reserve `!` for functions with side effects. Keep public APIs small and push parsing or transport details into private helpers.

## Testing Guidelines
Tests use `clojure.test`; HTTP behavior is isolated with `clj-http.fake`. Name test namespaces with the `_test.clj` suffix and group assertions with `deftest` plus `testing`. Add or update tests whenever request validation, config handling, or chat response parsing changes. Prefer deterministic fake routes over live Ollama calls.

## Commit & Pull Request Guidelines
Recent commits use short, direct summaries such as `Improving validations` and `fixing typos and docs`. Keep commit subjects brief, imperative or action-oriented, and focused on one change. For pull requests, include:

- A short description of the behavior change.
- Linked issue or context when applicable.
- Test evidence, for example `lein test`.
- Example request/response snippets when API behavior changes.

## Configuration Tips
Keep local overrides out of committed source when possible. The default endpoint is defined in `resources/config.edn` as `http://localhost:11434`; tests may temporarily replace it through `ollama.cli.config/config!`.
