(ns population.counties
  (:gen-class))
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(def uri "datomic:dev://localhost:4334/population")

(def conn (d/connect uri))

(defn read-csv "Reads CSV" []
  (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn get-state-entity [fips]
  (d/q '[:find ?e
         :in $ [?fips]
         :where
         [?e :map.state/fips ?fips]
        ]
       (db conn)
       [fips])
  )

(defn get-state-id [fips]
  (first (first (get-state-entity fips)))
  )


(defn buildCounty "Builds a county transact from a record" [record]
  (let [state (format "%02d" (read-string (get record "STATE")))
        county (format "%04d" (read-string (get record "COUNTY")))
        geoid (str state county)]
    {:db/id (d/tempid :db.part/user)
     :map.county/name (get record "CTYNAME")
     :map.county/fips (read-string (get record "COUNTY"))
     :map.county/geoid geoid
     :map.county/map.state (get-state-id (read-string (get record "STATE")))
     }))

(buildCounty (nth (read-csv) 225))
(get (buildCounty (second (read-csv))) :db/id)

;;(map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv)))

@(d/transact conn (map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))



