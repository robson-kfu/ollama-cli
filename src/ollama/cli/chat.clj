(ns ollama.cli.chat
  "High-level client for Ollama's `/api/chat` endpoint."
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
       "Failed to parse JSON in chat response."
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

(defn- request-stream [{:keys [stream is-stream is-stram]}]
  (cond
    (some? stream) stream
    (some? is-stream) is-stream
    (some? is-stram) is-stram
    :else true))

(defn- ->payload [{:keys [model
                          messages
                          tools
                          format
                          think
                          keep_alive
                          logprobs
                          top_logprobs
                          options
                          opts]}
                  is-stream]
  (json/generate-string
   (cond-> {:model model
            :messages messages
            :stream is-stream}
     (some? tools) (assoc :tools tools)
     (some? format) (assoc :format format)
     (some? think) (assoc :think think)
     (some? keep_alive) (assoc :keep_alive keep_alive)
     (some? logprobs) (assoc :logprobs logprobs)
     (some? top_logprobs) (assoc :top_logprobs top_logprobs)
     (some? (or options opts)) (assoc :options (or options opts)))))

(def headers
  {:content-type "application/json"})

(defn chat
  "Send a chat request to Ollama.

  `request` accepts the keys defined by `ollama.cli.schemas/chat-request`,
  including `:model`, `:messages`, optional `:tools`, and optional generation
  options.

  Returns a lazy sequence of parsed stream events when streaming is enabled, or
  a single parsed response map when streaming is disabled."
  [request]
  {:pre  [(s/valid? ::os/chat-request request)]
   :post [(s/valid? ::os/chat-response %)]}
  (let [is-stream (request-stream request)]
    (->>
     {:body    (->payload request is-stream)
      :headers headers}
     (core/stream-payload is-stream)
     (client/post (str (config/url) "/api/chat"))
     (parse-response is-stream))))

(comment
  (def model "phi3")
  (def messages [{:role    "user",
                  :content "Just experimenting, respond with an \"Hello, World\"!"}])
  (def opts {})
  (def is-stream true)
  (def response (chat {:model model :messages messages}))
  (class response)

  (doseq [s response]
    (apply str s))

  (prn))
