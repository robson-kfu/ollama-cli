(ns ollama.cli.schemas
  (:require [clojure.spec.alpha :as s]))

(defn- lazy-seq? [s] (instance? clojure.lang.LazySeq s))
(defn- role? [s]
  (some? (some #(= % s) ["user" "assistant" "system" "tool"])))
(defn- think-level? [s]
  (some? (some #(= % s) ["high" "medium" "low"])))

(s/def ::model string?)
(s/def ::role (s/and string? role?))
(s/def ::content string?)
(s/def ::tool_calls (s/coll-of map?))
(s/def ::message
       (s/keys :req-un [::role ::content]
               :opt-un [::images ::tool_calls]))
(s/def ::messages (s/coll-of ::message :kind vector? :min-count 1))
(s/def ::stream boolean?)
(s/def ::is-stream boolean?)
(s/def ::is-stram boolean?)
(s/def ::opts map?)
(s/def ::tools (s/coll-of map? :kind vector?))
(s/def ::images (s/nilable (s/coll-of string?)))
(s/def ::chat-request
       (s/keys :req-un [::model ::messages]
               :opt-un [::stream
                        ::is-stream
                        ::is-stram
                        ::opts
                        ::tools
                        ::format
                        ::think
                        ::keep_alive
                        ::options
                        ::logprobs
                        ::top_logprobs]))
(s/def ::chat-event map?)
(s/def ::chat-response
       (s/or :events (s/and lazy-seq? (s/coll-of ::chat-event :kind seq?))
             :event ::chat-event))

(s/def ::prompt string?)
(s/def ::suffix string?)
(s/def ::format (s/or :string string? :schema map?))
(s/def ::system string?)
(s/def ::think (s/or :enabled boolean? :level (s/and string? think-level?)))
(s/def ::raw boolean?)
(s/def ::keep_alive (s/or :string string? :number number?))
(s/def ::options map?)
(s/def ::logprobs boolean?)
(s/def ::top_logprobs int?)

(s/def ::generate-request
       (s/keys :req-un [::model]
               :opt-un [::prompt
                        ::suffix
                        ::images
                        ::format
                        ::system
                        ::stream
                        ::think
                        ::raw
                        ::keep_alive
                        ::options
                        ::logprobs
                        ::top_logprobs]))
(s/def ::generate-event map?)
(s/def ::generate-response
       (s/or :events (s/coll-of ::generate-event :kind seq?)
             :event ::generate-event))
