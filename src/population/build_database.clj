(ns population.build-database
  (:require [csv-map.core :as csv]
            [datomic.api :only [q db] :as d]
            [population.config :as config]))

(def schema-tx (read-string (slurp "/Users/pkimbrel/Local/src/population/resources/population-schema.edn")))
(def county-csv (csv/parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn get-state-entity [db fips]
  (d/q '[:find ?e
         :in $ [?fips]
         :where
         [?e :map.state/fips ?fips]
         ]
       db [fips])
  )

(defn get-state-id [db fips]
  (first (first (get-state-entity db fips)))
  )

(defn buildState [record]
  {:db/id (d/tempid :db.part/user)
   :map.state/name (get record "STNAME")
   :map.state/fips (read-string (get record "STATE"))
   })

(defn buildCounty [db record]
  (let [state (format "%02d" (read-string (get record "STATE")))
        county (format "%03d" (read-string (get record "COUNTY")))
        geoid (str "g" state county)]
    {:db/id (d/tempid :db.part/user)
     :map.county/name (get record "CTYNAME")
     :map.county/fips (read-string (get record "COUNTY"))
     :map.county/geoid geoid
     :map.county/map.state (get-state-id db (read-string (get record "STATE")))
     }))

(defn process-states []
  (map #(buildState %)
       (filter
        #(= (read-string (get % "COUNTY")) 0)
        county-csv)))

(defn process-counties [db]
  (map #(buildCounty db %)
       (filter
        #(not= (read-string (get % "COUNTY")) 0)
        county-csv)))

(defn reset-database "Resets the database.  NOTE: This will destroy any open connections" []
  (d/delete-database config/db-uri)
  (d/create-database config/db-uri)
  @(d/transact (d/connect config/db-uri) schema-tx)
)

(defn build-database "Builds the inital database." []
    (let [conn (d/connect config/db-uri)]
        @(d/transact conn (process-states))
        @(d/transact conn (process-counties (d/db conn)))))


;;(buildState (first (read-csv)))
;;(get (buildState (first (read-csv))) :db/id)
;;(d/tempid :db.part/user)
;;(buildCounty (nth (read-csv) 225))
;;(get (buildCounty (second (read-csv))) :db/id)
;;(map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv)))
