(ns ollama.cli.embed
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
  "Generate embeddings with the Ollama API.
   Returns a parsed response map with the model metadata and embedding vectors."
  [request]
  {:pre  [(s/valid? ::os/embed-request request)]
   :post [(s/valid? ::os/embed-response %)]}
  (->>
   {:body    (->payload request)
    :headers headers}
   (client/post (str (config/url) "/api/embed"))
   (parse-response)))
