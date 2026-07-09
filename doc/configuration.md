# Configuration

The library reads default endpoint settings from `resources/config.edn`.

## Inspecting configuration

```clojure
(require '[ollama.cli.config :as config])

(config/all-config)
(config/url)
```

## Overriding configuration at runtime

```clojure
(require '[ollama.cli.config :as config])

(config/config! {:host "http://remote-host"
                 :port "11434"})
```

## Resetting to defaults

```clojure
(config/reset-config!)
```
