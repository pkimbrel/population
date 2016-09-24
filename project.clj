(defproject population-data "0.1.0-SNAPSHOT"
  :description "Population map, reduce, filter example"
  :url "http://github.com/pkimbrel/population"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [com.datomic/datomic-pro "0.9.5394"]
                 [org.clojure/data.csv "0.1.3"]
                 [csv-map "0.1.2"]
                 ]
  :main ^:skip-aot population.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
