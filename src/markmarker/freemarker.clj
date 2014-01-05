(ns markmarker.freemarker
  (:import [freemarker.template TemplateDirectiveModel Configuration]
           [freemarker.cache FileTemplateLoader]
           [org.pegdown PegDownProcessor]
           [java.io File]))

(defn- render-body [tbody]
  (with-open [w (java.io.StringWriter.)]
    (do
      (.render tbody w)
      (str w))))

(def markdown-directive
  (proxy [TemplateDirectiveModel] []
     (execute [env params loopVars body]
       (.write (.getOut env) (.markdownToHtml (PegDownProcessor.) (render-body body))))))

(defn- render-template [template data filename]
  (with-open [w (clojure.java.io/writer filename)]
    (.process template data w)))

(defn create-configuration [template-dir]
  (doto (Configuration.)
    (.setDirectoryForTemplateLoading (File. template-dir))
    (.setSharedVariable "markdown" markdown-directive)))

(defn render [cfg template data out]
  (do
    (println "rendering" template "into" out)
    (clojure.java.io/make-parents out)
    (render-template (.getTemplate cfg template) data out)))


