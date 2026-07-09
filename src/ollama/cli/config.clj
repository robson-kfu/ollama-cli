(ns ollama.cli.config
  "Runtime configuration for the Ollama endpoint used by this library."
  (:require [aero.core :refer [read-config]]
             [clojure.java.io :as io]))

(defonce ^:private initial-config (read-config (io/resource "config.edn")))

(defonce config (atom initial-config))

(defn config!
  "Replace the current runtime configuration map."
  [new-config]
  (reset! config new-config))

(defn reset-config!
  "Reset runtime configuration to the defaults from `resources/config.edn`."
  []
  (reset! config (read-config (io/resource "config.edn"))))

(defn all-config
  "Return the current runtime configuration map."
  []
  @config)

(defn host
  "Return the configured Ollama host."
  []
  (:host @config))

(defn port
  "Return the configured Ollama port."
  []
  (:port @config))

(defn url
  "Return the base URL used for Ollama API requests."
  []
  (str (:host @config) ":" (:port @config)))
