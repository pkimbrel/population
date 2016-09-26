(ns population.core
  (:gen-class))
(require '[population.build-database :as db])
(require '[population.population-00-10 :as pop1])
(require '[population.population-10-15 :as pop2])
(use 'clojure.pprint)

(defn rebuild-database []
  (db/reset-database)
  (db/reload-database)
  )

(defn load-population []
  (pop1/transact-all)
  (pop2/transact-all)
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;(rebuild-database)
;;(load-population)




