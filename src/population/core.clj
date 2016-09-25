(ns population.core
  (:gen-class))
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(def uri "datomic:dev://localhost:4334/population")

(def conn (d/connect uri))

(defn read-csv "Reads CSV" []
  (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(comment defn buildCounty "Builds a county transact from a record" [record]
  (let [state (format "%02d" (read-string (get record "STATE")))
        county (format "%04d" (read-string (get record "COUNTY")))
        geoid (str state county)]
    {:db/id #db/id[:db.part/user]
     :map.county/name (get record "CTYNAME")
     :map.county/fips county
     :map.county/geoid geoid
     }))

(comment defn buildState "Builds a state transact from a record" [record]
  (let [state (format "%02d" (read-string (get record "STATE")))]
    {:db/id (d/tempid :db.part/user)
     :map.state/name (get record "STNAME")
     :map.state/fips state
     }))


(get (first (read-csv)) "COUNTY")

(comment buildState (first (read-csv)))
(comment buildCounty (second (read-csv)))

(comment get (buildState (first (read-csv))) :db/id)

;;@(d/transact conn (map buildState (filter #(= (read-string (get % "COUNTY")) 0) (read-csv))))

(d/tempid :db.part/user)
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
