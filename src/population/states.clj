(ns population.states
  (:gen-class))
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(def uri "datomic:dev://localhost:4334/population")

(def conn (d/connect uri))

(defn read-csv "Reads CSV" []
  (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn buildState "Builds a state transact from a record" [record]
  {:db/id (d/tempid :db.part/user)
   :map.state/name (get record "STNAME")
   :map.state/fips (read-string (get record "STATE"))
   })

(buildState (first (read-csv)))
(get (buildState (first (read-csv))) :db/id)

@(d/transact conn (map buildState (filter #(= (read-string (get % "COUNTY")) 0) (read-csv))))

(d/tempid :db.part/user)


