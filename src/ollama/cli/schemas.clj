(ns ollama.cli.schemas
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::model string?)
(spec/def ::role
          (spec/and string? #(contains? ["user" "assistant" "system"] %)))
(spec/def ::content string?)
(spec/def ::message (spec/keys :req [::role ::content]))
(spec/def ::messages (spec/coll-of ::message :kind vector? :min-count 1))
(spec/def ::is-stream boolean?)
(spec/def ::opts map?)
(spec/def :chat/request
          (spec/keys :req [::messages ::model] :opt [::is-stream ::opts]))