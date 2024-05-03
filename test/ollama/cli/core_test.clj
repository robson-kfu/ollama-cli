(ns ollama.cli.core-test
  (:require [clojure.test :refer :all]
            [ollama.cli.core :as core]))

(deftest test-stream-payload
  (testing "Testing when stream is true"
           (let [result (core/stream-payload true {:any "any"})]
             (is (= :stream (:as result)))))

  (testing "Testing when stream is false"
           (let [result (core/stream-payload false {:any "any"})]
             (is (nil? (:as result)))))

  (testing "Testing when stream is nil"
           (let [result (core/stream-payload nil {:any "any"})]
             (is (nil? (:as result))))))