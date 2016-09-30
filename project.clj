(defproject population-data "0.1.0-SNAPSHOT"
  :description "Population map, reduce, filter example"
  :url "http://github.com/pkimbrel/population"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [com.datomic/datomic-pro "0.9.5394"]
                 [csv-map "0.1.2"]
                 [hiccup "1.0.5"]
                 [compojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [ring/ring-defaults "0.2.1"]
                 ]
  :ring {:handler population.core/app}
  :target-path "target/%s"
  :plugins [[lein-ring "0.9.7"]]
  :profiles {:uberjar {:aot :all}})
