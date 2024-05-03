(ns ollama.cli.config
  (:require [aero.core :refer [read-config]]
             [clojure.java.io :as io]))

(defonce ^:private initial-config (read-config (io/resource "config.edn")))

(defonce config (atom initial-config))

(defn config! [new-config]
  (reset! config new-config))

(defn reset-config! []
  (reset! config (read-config (io/resource "config.edn"))))

(defn all-config []
  @config)

(defn host [] (:host @config))
(defn port [] (:port @config))
(defn url [] (str (:host @config) ":" (:port @config)))