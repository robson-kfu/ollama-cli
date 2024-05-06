(ns ollama.cli.chat-test
  (:require [clojure.test :refer :all]
            [ollama.cli.chat :refer :all]
            [clj-http.fake :as http-fake]
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

(def model "phi3")
(def messages [{:role    "user",
                :content "Just experimenting, respond with an \"Hello, World\"!"}])

(deftest test-valid-stream-chat
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/chat") (fn [request]
                                     {:status  200
                                      :headers {"Content-Type" "application/json"}
                                      :body   mock-stream-body})}
   (testing "Chatting with stream option"
            (let [response (chat! {:model model :messages messages})]
              (is (= "Hello, World!" (apply str (map str response))))))))

(deftest test-valid-chat
  (http-fake/with-fake-routes-in-isolation
   {(str (config/url) "/api/chat") (fn [request]
                                     {:status  200
                                      :headers {"Content-Type" "application/json"}
                                      :body   mock-stream-body})}
   (testing "Chatting without stream option"
            (let [response (chat! {:model model :messages messages :is-stram false})]
              (is (= "Hello, World!" (apply str (map str response))))))))