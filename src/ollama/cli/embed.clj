(ns ollama.cli.embed
  "High-level client for Ollama's `/api/embed` endpoint."
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [clojure.spec.alpha :as s]
            [ollama.cli.config :as config]
            [ollama.cli.schemas :as os]))

(def headers
  {:content-type "application/json"})

(defn- parse-response [{:keys [body]}]
  (json/parse-string body true))

(defn- ->payload [{:keys [model
                          input
                          truncate
                          dimensions
                          keep_alive
                          options]}]
  (json/generate-string
   (cond-> {:model model
            :input input}
     (some? truncate) (assoc :truncate truncate)
     (some? dimensions) (assoc :dimensions dimensions)
     (some? keep_alive) (assoc :keep_alive keep_alive)
     (some? options) (assoc :options options))))

(defn embed
  "Request embeddings from Ollama.

  `request` accepts the keys defined by `ollama.cli.schemas/embed-request`,
  including `:model`, `:input`, and optional `:dimensions` or `:options`.

  Returns a parsed response map with model metadata and embedding vectors."
  [request]
  {:pre  [(s/valid? ::os/embed-request request)]
   :post [(s/valid? ::os/embed-response %)]}
  (->>
   {:body    (->payload request)
    :headers headers}
   (client/post (str (config/url) "/api/embed"))
   (parse-response)))
