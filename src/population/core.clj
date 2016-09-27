(ns population.core
  (:gen-class)
  (:require [datomic.api :only [q db] :as d])
  (:require [population.build-database :as db])
  (:require [population.population-00-10 :as pop10])
  (:require [population.population-10-15 :as pop15]))

(def db-uri "datomic:dev://localhost:4334/population")

(defn rebuild-database []
  (db/reset-database)
  (db/build-database))

(defn process-population []
  (let [db (d/db (d/connect db-uri))]
    (concat
     (pop10/process-years db)
     (pop15/process-years db))))

(defn load-population []
  @(d/transact (d/connect db-uri) (process-population))
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;(rebuild-database)
;;(process-population)

;;(pop10/process-years (d/db (d/connect db-uri)))
;;(spit "temp.edn" (with-out-str (pr (process-population))))
(process-population)
(load-population)





