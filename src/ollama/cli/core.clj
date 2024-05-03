(ns ollama.cli.core)

(defn stream-payload
  [is-stream payload]
  (if is-stream
    (assoc payload :as :stream)
    payload))