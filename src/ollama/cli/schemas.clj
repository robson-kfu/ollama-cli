(ns ollama.cli.schemas
  (:require [clojure.spec.alpha :as s]))

(defn- lazy-seq? [s] (instance? clojure.lang.LazySeq s))
(defn- role? [s]
  (some? (some #(= % s) ["user" "assistant" "system"])))

(s/def ::model string?)
(s/def ::role (s/and string? role?))
(s/def ::content string?)
(s/def ::message (s/keys :req-un [::role ::content]))
(s/def ::messages (s/coll-of ::message :kind vector? :min-count 1))
(s/def ::is-stream boolean?)
(s/def ::opts map?)
(s/def ::images (s/nilable (s/coll-of string?)))
(s/def ::request
       (s/keys :req-un [::model ::messages] :opt-un [::is-stram ::opts]))
(s/def ::response
       (s/or :lazy-seq-of-strings (s/and lazy-seq? (s/coll-of string? :kind seq?))
             :single-string #(or string? nil? %)))
