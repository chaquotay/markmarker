(ns markmarker.core
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:import  [java.text SimpleDateFormat])
  (:use markmarker.freemarker cheshire.core)
  (:gen-class))

(def index-template "index.ftl")
(def post-template "post.ftl")
(def date-format (SimpleDateFormat. "yyyy-MM-dd"))

(defn- parse-date [text]
  (.parse date-format text))

(defn- read-site [file]
  (parse-stream (io/reader file) false))

(defn- rel-post-url [title date]
  (format "%1$tY/%1$tm/%1$td/%2$s.html" date (clojure.string/join "-" (re-seq #"\w+" (.toLowerCase title)))))

(defn- prepare-post [post base-dir]
  (let [date (parse-date (post "date"))]
    (assoc post
      "rel-url" (rel-post-url (post "title") date)
      "text" (slurp (str base-dir "/posts/" (post "src"))))))

(defn- prepare-site [site base-dir]
  (assoc site "posts" (map #(prepare-post % base-dir) (site "posts"))))

(defn render-site [site template-dir to-dir]
  (let [cfg (create-configuration template-dir)]
    (do
      (render cfg index-template site (str to-dir "/index.html"))
      (doseq [post (site "posts")]
        (render cfg post-template {"post" post} (str to-dir "/" (post "rel-url")))))))

(defn render-site-default [base-dir]
  (render-site (prepare-site (read-site (str base-dir "/site.json")) base-dir) base-dir (str base-dir "/out")))

(def cli-options
  [["-d" "--directory DIRECTORY" "Home directory"
    :default "markmarker-home"
    ]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Generates a blog-like website."
        ""
        "Usage: markmarker [options]"
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the manual page for more information."]
    (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
    (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main
  "Main entry point"
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      :else (render-site-default (:directory options)))))


