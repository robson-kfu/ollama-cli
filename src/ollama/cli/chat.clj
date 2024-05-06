(ns ollama.cli.chat
  (:require [ollama.cli.core :as core]
            [clojure.java.io :as io]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [ollama.cli.config :as config]
            [clojure.tools.logging :as log]
            [clojure.spec.alpha :as spec]))

(defn- extract-message-content [json-str]
  (try
    (-> json-str
        json/parse-string
        (get-in ["message" "content"]))
    (catch Exception e
      (log/error "Failed to parse JSON:" json-str "with error:" (.getMessage e))
      nil)))

(defn- parse-stream [stream]
  (with-open [rdr (io/reader stream)]
    (doall
     (map extract-message-content (line-seq rdr)))))

(defn- parse-response [is-stream {:keys [body]}]
  (if is-stream
    (parse-stream body)
    (extract-message-content body)))

(defn- ->payload [{:keys [model messages opts] :or {opts {}}} is-stream]
  (json/generate-string
   (assoc opts
          :model    model
          :messages messages
          :stream   is-stream)))

(def headers
  {:content-type "application/json"})

(defn chat!
  "Chat with the ollama API. Receive the model, messages, is-stream, and opts.
   When is-tream iquals true, returns a lazySeq of string.
   Check possibilities for models and opts at https://ollama.com/library"
  [{:keys [is-stream] :as request :or {is-stream true}}]
  {:pre [spec/valid? :chat/request request]}
  (->>
   {:body    (->payload request is-stream)
    :headers headers}
   (core/stream-payload is-stream)
   (client/post (str (config/url) "/api/chat"))
   (parse-response is-stream)))

(comment
  (def model "phi3")
  (def messages [{:role    "user",
                  :content "Just experimenting, respond with an \"Hello, World\"!"}])
  (def opts {})
  (def is-stream true)
  (def response (chat! {:model model :messages messages }))
  (class response)

  (doseq [s response]
    (apply str s))

  (prn))