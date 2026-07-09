[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.robson-kfu/ollama-cli.svg?include_prereleases)](https://clojars.org/org.clojars.robson-kfu/ollama-cli)
[![cljdoc badge](https://cljdoc.org/badge/org.clojars.robson-kfu/ollama-cli)](https://cljdoc.org/d/org.clojars.robson-kfu/ollama-cli)

# ollama/cli

`ollama/cli` is a small Clojure client for Ollama's HTTP API. It provides
focused functions for chat, text generation, and embeddings while keeping the
request payloads close to Ollama's native API.

## Features

- Chat completions via `ollama.cli.chat/chat`
- Text generation via `ollama.cli.generate/generate`
- Embeddings via `ollama.cli.embed/embed`
- Runtime endpoint configuration via `ollama.cli.config`

## Getting Started

### Prerequisites

Before you can use `ollama/cli`, ensure you have the Clojure CLI installed on your machine:

- [Clojure CLI](https://clojure.org/guides/getting_started)

### Development

Run the full test suite with:

```bash
clojure -M:test
```

Build the checked-in snapshot release metadata with:

```bash
clojure -T:build jar
```

Install the snapshot to your local Maven cache with:

```bash
clojure -T:build install
```

Publish the snapshot to Clojars with a deploy token:

```bash
clojure -T:build jar
./bin/publish-clojars
```

The `.env` file should define `CLOJARS_USERNAME` and `CLOJARS_DEPLOY_TOKEN`. The publish wrapper maps that token to the env var expected by `deps-deploy`.

### Usage

Here are basic examples of how to use `ollama/cli` in your Clojure application:

```clojure
org.clojars.robson-kfu/ollama-cli {:mvn/version "0.0.3-SNAPSHOT"}
```

```clojure
(ns my-app
  (:require [ollama.cli.chat :as chat]
            [ollama.cli.embed :as embed]
            [ollama.cli.generate :as generate]))

(def messages
  [{:role "user"
    :content "Why is the sky blue?"}])
(def model "phi3")

(def response
  (chat/chat {:model model :messages messages}))

(doseq [chunk response]
  (print (str chunk)))

(def generated
  (generate/generate {:model model
                      :prompt "Summarize Rayleigh scattering"
                      :stream false
                      :think true}))

(:response generated)

(def embeddings
  (embed/embed {:model "embeddinggemma"
                :input ["Why is the sky blue?"
                        "Why is the grass green?"]}))

(:embeddings embeddings)
```

## Documentation

Additional guides:

- [Getting Started](doc/getting-started.md)
- [Configuration](doc/configuration.md)

## Contributing

Contributions to `ollama-cli` are welcome and appreciated. Please refer to the CONTRIBUTING.md for guidelines on how to make contributions.

## License

`ollama-cli` is open source and is licensed under the MIT License. See the LICENSE file for more details.

## Support

If you encounter any issues or have questions, feel free to open an issue on the project's GitHub repository or contact the maintainers directly.

Thank you for using or considering `ollama-cli`!
