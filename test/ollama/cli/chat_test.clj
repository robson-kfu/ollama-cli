(ns ollama.cli.chat-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [clj-http.fake :as http-fake]
            [ollama.cli.chat :refer :all]
            [ollama.cli.config :as config]))

(def test-config
  {:host "http://localhost"
   :port "11435"})

(defn setup [test-fn]
  ;; setup code
  (config/config! test-config)
  (test-fn)
  ;; teardown code
  (config/reset-config!))

(use-fixtures :once setup)

(def mock-stream-body
  "{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:01.771673949Z\",\"message\":{\"role\":\"assistant\",\"content\":\"Hello\"},\"done\":false}
   \r\n{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:01.904817583Z\",\"message\":{\"role\":\"assistant\",\"content\":\",\"},\"done\":false}
   \r\n{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:02.039907156Z\",\"message\":{\"role\":\"assistant\",\"content\":\" World\"},\"done\":false}
   \r\n{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:02.167411947Z\",\"message\":{\"role\":\"assistant\",\"content\":\"!\"},\"done\":false}
   \r\n{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:02.302682575Z\",\"message\":{\"role\":\"assistant\",\"content\":\"\"},\"done\":true,\"total_duration\":818929500,\"load_duration\":1349262,\"prompt_eval_duration\":144865000,\"eval_count\":5,\"eval_duration\":530957000}")

(def mock-body
  "{\"model\":\"phi3\",\"created_at\":\"2024-05-03T19:19:02.302682575Z\",\"message\":{\"role\":\"assistant\",\"content\":\"Hello, World!\"},\"done\":true}")

(def model "phi3")
(def messages [{:role    "user",
                :content "Just experimenting, respond with an \"Hello, World\"!"}])
(def invalid-messages [{:role    "unknow",
                        :content 123}])

(deftest test-valid-stream-chat
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/chat") (fn [request]
                                     {:status  200
                                      :headers {"Content-Type" "application/json"}
                                      :body    mock-stream-body})}
   (testing "Valid chatting with stream option"
            (let [response (chat {:model model :messages messages})]
              (is (= "Hello, World!"
                     (apply str (map #(get-in % [:message :content]) response))))
              (is (= false (:done (first response))))
              (is (= true (:done (last response))))))))

(deftest test-valid-chat
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/chat") (fn [request]
                                     {:status  200
                                      :headers {"Content-Type" "application/json"}
                                      :body    mock-body})}
   (testing "Valid chatting without stream option"
            (let [response (chat {:model model :messages messages :stream false})]
              (is (= "Hello, World!" (get-in response [:message :content])))
              (is (= true (:done response)))))
   (testing "Legacy is-stream key still disables streaming"
            (let [response (chat {:model model :messages messages :is-stream false})]
              (is (= "Hello, World!" (get-in response [:message :content])))))
   (testing "Legacy typo key still disables streaming"
            (let [response (chat {:model model :messages messages :is-stram false})]
              (is (= "Hello, World!" (get-in response [:message :content])))))
   (testing "Invalid chatting without stream option"
            (is
              (thrown? java.lang.AssertionError
                       (chat {:model model :messages invalid-messages :is-stream false}))))))

(deftest test-chat-payload-contains-documented-fields
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/chat") (fn [request]
                                     (let [payload (json/parse-string (slurp (:body request)) true)]
                                       (is (= false (:stream payload)))
                                       (is (= "json" (:format payload)))
                                       (is (= "medium" (:think payload)))
                                       (is (= {:temperature 0.1} (:options payload)))
                                       (is (= true (:logprobs payload)))
                                       (is (= 3 (:top_logprobs payload)))
                                       {:status  200
                                        :headers {"Content-Type" "application/json"}
                                        :body    mock-body}))}
   (chat {:model model
           :messages messages
           :stream false
           :format "json"
           :think "medium"
           :options {:temperature 0.1}
           :logprobs true
           :top_logprobs 3})))

(deftest test-chat-payload-supports-legacy-opts
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/chat") (fn [request]
                                     (let [payload (json/parse-string (slurp (:body request)) true)]
                                       (is (= {:temperature 0.2} (:options payload)))
                                       {:status  200
                                        :headers {"Content-Type" "application/json"}
                                        :body    mock-body}))}
   (chat {:model model
           :messages messages
           :stream false
           :opts {:temperature 0.2}})))
