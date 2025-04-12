(defproject org.clojars.robson-kfu/ollama-cli "0.0.1-SNAPSHOT"
  :description "Ollama clojure client"
  :url "https://github.com/robson-kfu/ollama-cli"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [clj-http "3.13.0"]
                 [cheshire "5.10.0"]
                 [aero "1.1.6"]
                 [clj-http-fake "1.0.4"]]
  :plugins [[lein-pprint "1.3.2"]])
