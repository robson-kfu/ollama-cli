(ns ollama.cli.generate-test
  (:require [cheshire.core :as json]
            [clj-http.fake :as http-fake]
            [clojure.test :refer :all]
            [ollama.cli.config :as config]
            [ollama.cli.generate :refer :all]))

(def test-config
  {:host "http://localhost"
   :port "11435"})

(defn setup [test-fn]
  (config/config! test-config)
  (test-fn)
  (config/reset-config!))

(use-fixtures :once setup)

(def model "phi3")
(def prompt "Why is the sky blue?")

(def mock-stream-body
  "{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:01.771673949Z\",\"response\":\"Hello\",\"thinking\":\"Check facts\",\"done\":false}
   \r\n{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:01.904817583Z\",\"response\":\" world\",\"done\":false}
   \r\n{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:02.302682575Z\",\"response\":\"!\",\"done\":true}")

(def mock-body
  "{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:02.302682575Z\",\"response\":\"Rayleigh scattering\",\"thinking\":\"Atmospheric particles scatter short wavelengths\",\"done\":true}")

(deftest test-valid-stream-generate
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/generate") (fn [request]
                                         (let [payload (json/parse-string (slurp (:body request)) true)]
                                           (is (= model (:model payload)))
                                           (is (= prompt (:prompt payload)))
                                           (is (= true (:stream payload)))
                                           (is (= "medium" (:think payload)))
                                           {:status  200
                                            :headers {"Content-Type" "application/json"}
                                            :body    mock-stream-body}))}
   (testing "Valid generation with stream option"
     (let [response (generate! {:model model
                                :prompt prompt
                                :think "medium"})]
       (is (= "Hello world!" (apply str (map :response response))))
       (is (= "Check facts" (:thinking (first response))))))))

(deftest test-valid-generate
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/generate") (fn [request]
                                         (let [payload (json/parse-string (slurp (:body request)) true)]
                                           (is (= false (:stream payload)))
                                           (is (= false (:logprobs payload)))
                                           {:status  200
                                            :headers {"Content-Type" "application/json"}
                                            :body    mock-body}))}
   (testing "Valid generation without stream option"
     (let [response (generate! {:model model
                                :prompt prompt
                                :stream false
                                :logprobs false})]
       (is (= "Rayleigh scattering" (:response response)))
       (is (= "Atmospheric particles scatter short wavelengths"
              (:thinking response)))))))

(deftest test-invalid-generate
  (testing "Model is required"
           (is (thrown? java.lang.AssertionError
                        (generate! {:prompt prompt :stream false})))))
