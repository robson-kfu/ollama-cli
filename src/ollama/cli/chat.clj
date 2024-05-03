(ns ollama.cli.chat
  (:require [ollama.cli.core :as core]
            [clojure.java.io :as io]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [ollama.cli.config :as config]
            [clojure.tools.logging :as log]
            [clojure.spec.alpha :as spec]))

(defn- content [json]
  (get-in json ["message" "content"]))

(defn- extract-message-content [json-str]
  (try
    (-> json-str
        json/parse-string
        content)
    (catch Exception e
      (log/error "Failed to parse JSON:" json-str "with error:" (.getMessage e))
      nil)))

(defn- parse-stream [stream]
  "Parses a stream of JSON strings, each on a new line, extracting contents from each."
  (with-open [rdr (io/reader stream)]
    (doall
     (map extract-message-content (line-seq rdr)))))

(defn- parse-content [response]
  (extract-message-content response))

(defn- parse-response [is-stream {:keys [body]}]
  (if is-stream
    (parse-stream body)
    (parse-content body)))

(defn- payload [{:keys [model messages opts] :or {opts {}}} is-stream]
  (json/generate-string
   (assoc opts
          :model    model
          :messages messages
          :stream   is-stream)))

(def headers
  {:content-type "application/json"})

(defn- internal-request [{:keys [is-stream] :as request :or {is-stream true}}]
  (->>
   {:body    (payload request is-stream)
    :headers headers}
   (core/stream-payload is-stream)
   (client/post (str (config/url) "/api/chat"))
   (parse-response is-stream)))

(defn chat!
  "Chat with the ollama API. Receive the model, messages, is-stream, and opts.
   When is-tream iquals true, returns a lazySeq of string.
   Check possibilities for models and opts at https://ollama.com/library"
  [request]
  {:pre [spec/valid? :chat/request request]}
  (internal-request request))

(comment
  (def model "phi3")
  (def messages [{:role    "user",
                  :content "Just experimenting, respond with an \"Hello, World\"!"}])
  (def opts {})
  (def is-stream true)
  (def response (chat! {:model model :messages messages :is-stream}))
  (class response)
  (doseq [s response]
    (print (str s)))

  (prn))