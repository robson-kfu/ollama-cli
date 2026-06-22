(ns ollama.cli.embed-test
  (:require [cheshire.core :as json]
            [clj-http.fake :as http-fake]
            [clojure.test :refer :all]
            [ollama.cli.config :as config]
            [ollama.cli.embed :refer :all]))

(def test-config
  {:host "http://localhost"
   :port "11435"})

(defn setup [test-fn]
  (config/config! test-config)
  (test-fn)
  (config/reset-config!))

(use-fixtures :once setup)

(def model "embeddinggemma")
(def input "Why is the sky blue?")
(def multi-input ["Why is the sky blue?"
                  "Why is the grass green?"])

(def mock-body
  "{\"model\":\"embeddinggemma\",\"embeddings\":[[0.010071029,-0.0017594862,0.05007221],[0.04692972,0.054916814,0.008599704]],\"total_duration\":14143917,\"load_duration\":1019500,\"prompt_eval_count\":8}")

(deftest test-valid-embed
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/embed") (fn [request]
                                      (let [payload (json/parse-string (slurp (:body request)) true)]
                                        (is (= model (:model payload)))
                                        (is (= input (:input payload)))
                                        (is (= false (:truncate payload)))
                                        (is (= 128 (:dimensions payload)))
                                        (is (= "5m" (:keep_alive payload)))
                                        (is (= {:num_ctx 512} (:options payload)))
                                        {:status  200
                                         :headers {"Content-Type" "application/json"}
                                         :body    mock-body}))}
   (let [response (embed {:model model
                           :input input
                           :truncate false
                           :dimensions 128
                           :keep_alive "5m"
                           :options {:num_ctx 512}})]
     (is (= model (:model response)))
     (is (= 2 (count (:embeddings response))))
     (is (= 8 (:prompt_eval_count response))))))

(deftest test-valid-multi-input-embed
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/embed") (fn [request]
                                      (let [payload (json/parse-string (slurp (:body request)) true)]
                                        (is (= multi-input (:input payload)))
                                        {:status  200
                                         :headers {"Content-Type" "application/json"}
                                         :body    mock-body}))}
   (let [response (embed {:model model
                           :input multi-input})]
     (is (= model (:model response))))))

(deftest test-invalid-embed
  (testing "Model and input are required"
    (is (thrown? java.lang.AssertionError
                 (embed {:model model}))))
  (testing "Input must be a string or a non-empty vector of strings"
    (is (thrown? java.lang.AssertionError
                 (embed {:model model
                          :input []})))
    (is (thrown? java.lang.AssertionError
                 (embed {:model model
                          :input ["ok" 1]})))))
