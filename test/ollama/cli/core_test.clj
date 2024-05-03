(ns ollama.cli.core-test
  (:require [clojure.test :refer :all]
            [ollama.cli.core :as core]))

(deftest test-stream-payload-true
  (testing "Testing when stream is true"
           (let [result (core/stream-payload true {:any "any"})]
             (is (= :stream (:as result))))))

(deftest test-stream-payload-false
  (testing "Testing when stream is false"
           (let [result (core/stream-payload false {:any "any"})]
             (is (nil? (:as result))))))

(deftest test-stream-payload-nil
  (testing "Testing when stream is nil"
           (let [result (core/stream-payload nil {:any "any"})]
             (is (nil? (:as result))))))