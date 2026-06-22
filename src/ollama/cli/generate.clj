(ns ollama.cli.generate
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [ollama.cli.config :as config]
            [ollama.cli.core :as core]
            [ollama.cli.schemas :as os]))

(defn- extract-event [json-str]
  (try
    (json/parse-string json-str true)
    (catch Exception e
      (ex-info
       "Failed to parse JSON in generate response."
       {:response     json-str
        :errorMessage (.getMessage e)}
       e)
      nil)))

(defn- parse-stream [stream]
  (with-open [rdr (io/reader stream)]
    (doall
     (map extract-event
          (remove str/blank? (line-seq rdr))))))

(defn- parse-response [is-stream {:keys [body]}]
  (if is-stream
    (parse-stream body)
    (extract-event body)))

(defn- ->payload [{:keys [model
                          prompt
                          suffix
                          images
                          format
                          system
                          think
                          raw
                          keep_alive
                          options
                          logprobs
                          top_logprobs]}
                  is-stream]
  (json/generate-string
   (cond-> {:model model
            :stream is-stream}
     (some? prompt) (assoc :prompt prompt)
     (some? suffix) (assoc :suffix suffix)
     (some? images) (assoc :images images)
     (some? format) (assoc :format format)
     (some? system) (assoc :system system)
     (some? think) (assoc :think think)
     (some? raw) (assoc :raw raw)
     (some? keep_alive) (assoc :keep_alive keep_alive)
     (some? options) (assoc :options options)
     (some? logprobs) (assoc :logprobs logprobs)
     (some? top_logprobs) (assoc :top_logprobs top_logprobs))))

(def headers
  {:content-type "application/json"})

(defn generate
  "Generate text with the Ollama API.
   Returns a sequence of parsed stream events when :stream is true, or a single
   parsed response map when :stream is false."
  [{:keys [stream] :as request :or {stream true}}]
  {:pre  [(s/valid? ::os/generate-request request)]
   :post [(s/valid? ::os/generate-response %)]}
  (->>
   {:body    (->payload request stream)
    :headers headers}
   (core/stream-payload stream)
   (client/post (str (config/url) "/api/generate"))
   (parse-response stream)))

(comment
  (def response
    (generate {:model  "phi3"
                :prompt "Why is the sky blue?"
                :think  true}))

  (doseq [{:keys [response thinking]} response]
    (print response)
    (when thinking
      (print thinking))))
