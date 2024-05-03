(ns ollama.cli.config-test
  (:require [clojure.test :refer :all]
            [ollama.cli.config :as config]))

(deftest test-default-url
  (testing "Default ollama URL should be loaded from config.edn"
           (config/reset-config!)
           (is (= "http://localhost:11434" (config/url)))))

(deftest test-override-config
  (testing "New config should override default config"
           (config/config!
             {:host "http://new-host.com"
              :port "8080"})
           (is (= "http://new-host.com:8080" (config/url)))))