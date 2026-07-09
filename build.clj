(ns build
  (:require [clojure.string :as str]
            [clojure.tools.build.api :as b]))

(def lib 'org.clojars.robson-kfu/ollama-cli)
(def version "0.0.3-SNAPSHOT")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))
(def git-sha
  (-> (b/process {:command-args ["git" "rev-parse" "HEAD"]
                  :out :capture})
      :out
      str/trim))

(defn- scm-tag []
  (if (str/ends-with? version "-SNAPSHOT")
    git-sha
    (str "v" version)))

(def pom-template
  [[:description "A small Clojure client for Ollama chat, generate, and embedding APIs."]
   [:url "https://github.com/robson-kfu/ollama-cli"]
   [:developers
    [:developer
     [:name "Robson"]
     [:url "https://github.com/robson-kfu"]]]
   [:licenses
    [:license
     [:name "MIT"]
     [:url "https://opensource.org/licenses/MIT"]]]
   [:scm
    [:url "https://github.com/robson-kfu/ollama-cli"]
    [:connection "scm:git:https://github.com/robson-kfu/ollama-cli.git"]
    [:developerConnection "scm:git:ssh://git@github.com/robson-kfu/ollama-cli.git"]
    [:tag (scm-tag)]]])

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (clean nil)
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]
                :pom-data pom-template})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file})
  {:jar-file jar-file
   :version version})

(defn install [_]
  (jar nil)
  (b/install {:class-dir class-dir
              :lib lib
              :version version
              :basis basis
              :jar-file jar-file})
  {:jar-file jar-file
   :version version})
