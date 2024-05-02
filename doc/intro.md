# How to configure
## Overriding the configuration
```clojure
(ns external-project.core
  (:require [ollama-cli.config :as config]))
;; Overriding default (local) configuration
(config/set-config! {:host "http://new-host.com"
                     :port "8080"})
```
