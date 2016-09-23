(ns population.core
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

((defn buildTransact "Builds a transact from a record" [record]
    { :db/id #db/id[:db.part/user] }))




(get (first (read-csv)) "CTYNAME")

(buildTransact (first (read-csv)))
;;(def users
;;  (q '[:find ?e ?login
;;       :where
;;       [?e :bo.user/login ?login]
;;       ]
;;     (db conn)))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
