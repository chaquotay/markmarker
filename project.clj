(defproject markmarker "0.1.0-SNAPSHOT"
  :description "Generates a blog-like website."
  :url "https://github.com/chaquotay/markmarker"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [org.clojure/clojure "1.5.1"]
                  [org.freemarker/freemarker "2.3.20"]
                  [org.pegdown/pegdown "1.4.1"]
                  [cheshire "5.3.0"]
                  [org.clojure/tools.cli "0.3.1"]]
  :main ^:skip-aot markmarker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
