# Getting Started

`ollama/cli` is a thin wrapper around Ollama's HTTP API. The public entry
points mirror the main API areas:

- `ollama.cli.chat/chat`
- `ollama.cli.generate/generate`
- `ollama.cli.embed/embed`

The examples below assume a local Ollama instance running on the default
endpoint from `resources/config.edn`, `http://localhost:11434`.

## Chat

```clojure
(require '[ollama.cli.chat :as chat])

(chat/chat
 {:model "phi3"
  :stream false
  :messages [{:role "user"
              :content "Explain Rayleigh scattering in one paragraph."}]})
```

Set `:stream true` to receive a lazy sequence of parsed events instead of a
single response map.

## Generate

```clojure
(require '[ollama.cli.generate :as generate])

(generate/generate
 {:model "phi3"
  :stream false
  :prompt "Write a haiku about distributed systems."})
```

## Embed

```clojure
(require '[ollama.cli.embed :as embed])

(embed/embed
 {:model "embeddinggemma"
  :input ["first text" "second text"]})
```

## Validation

The public API functions use `clojure.spec` pre/post conditions. Invalid input
will fail fast before an HTTP request is made.
