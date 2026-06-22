(ns ollama.cli.test-runner
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.test :as test]))

(defn- test-file? [file]
  (and (.isFile file)
       (str/ends-with? (.getName file) "_test.clj")))

(defn- path->namespace [root-path file]
  (let [file-path (.getPath file)
        relative-path (subs file-path (inc (count root-path)))
        namespace-path (subs relative-path 0 (- (count relative-path) 4))]
    (-> namespace-path
        (str/replace "_" "-")
        (str/replace \/ \.)
        symbol)))

(defn- discover-test-namespaces []
  (let [test-root (io/file "test")
        root-path (.getPath test-root)]
    (->> (file-seq test-root)
         (filter test-file?)
         (map #(path->namespace root-path %))
         sort)))

(defn -main [& _args]
  (let [namespaces (discover-test-namespaces)]
    (doseq [ns-sym namespaces]
      (require ns-sym))
    (let [{:keys [fail error]} (apply test/run-tests namespaces)]
      (when (pos? (+ fail error))
        (System/exit 1)))))
