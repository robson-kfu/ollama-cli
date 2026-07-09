(ns ollama.cli.core
  "Shared request helpers used by the public API namespaces.")

(defn stream-payload
  "Add the `:as :stream` clj-http option when streaming is enabled."
  [is-stream payload]
  (if is-stream
    (assoc payload :as :stream)
    payload))
