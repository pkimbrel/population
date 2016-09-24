(ns population.counties
  (:gen-class))
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(def uri "datomic:dev://localhost:4334/population")

(def conn (d/connect uri))

(defn read-csv "Reads CSV" []
  (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn buildCounty "Builds a county transact from a record" [record]
  (let [state (format "%02d" (read-string (get record "STATE")))
        cou:db/id #db/id[:db.part/user idnty (format "%04d" (read-string (get record "COUNTY")))
        geoid (str state county)]
    {:db/id #db/id[:db.part/user]
     :map.county/name (get record "CTYNAME")
     :map.county/fips county
     :map.county/geoid geoid
     }))

(defn buildState "Builds a state transact from a record" [record]
  (let [state (format "%02d" (read-string (get record "STATE")))]
    {:db/id #db/id[:db.part/user]
     :map.state/name (get record "STNAME")
     :map.state/fips state
     }))


(defn buildTransact "Builds a transact from given record" [record]
  (cond
    (= (read-string (get record "COUNTY")) 0) (buildState record)
    :else (buildCounty record)
    ))

(read-string "9")
(get (first (read-csv)) "COUNTY")

(buildState (first (read-csv)))
(buildCounty (second (read-csv)))

(buildTransact (first (read-csv)))
(buildTransact (second (read-csv)))

(get (buildState (first (read-csv))) :db/id)

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
