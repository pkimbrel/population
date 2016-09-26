(ns population.build-database)
(require '[datomic.api :as d])
(use 'csv-map.core)

(def db-uri "datomic:dev://localhost:4334/population")
(def conn (atom (d/connect db-uri)))

(def schema-tx (read-string (slurp "/Users/pkimbrel/Local/src/population/resources/population-schema.edn")))
(def population-csv (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn get-state-entity [fips]
  (d/q '[:find ?e
         :in $ [?fips]
         :where
         [?e :map.state/fips ?fips]
        ]
       (d/db @conn)
       [fips])
  )

(defn get-state-id [fips]
  (first (first (get-state-entity fips)))
  )

(defn buildState [record]
    {:db/id (d/tempid :db.part/user)
    :map.state/name (get record "STNAME")
    :map.state/fips (read-string (get record "STATE"))
    })

(defn buildCounty [record]
  (let [state (format "%02d" (read-string (get record "STATE")))
        county (format "%03d" (read-string (get record "COUNTY")))
        geoid (str "g" state county)]
    {:db/id (d/tempid :db.part/user)
     :map.county/name (get record "CTYNAME")
     :map.county/fips (read-string (get record "COUNTY"))
     :map.county/geoid geoid
     :map.county/map.state (get-state-id (read-string (get record "STATE")))
     }))

(defn transact-states []
   @(d/transact
     @conn
     (map buildState
          (filter
           #(= (read-string (get % "COUNTY")) 0)
            population-csv))))

(defn transact-counties []
  @(d/transact
    @conn
    (map buildCounty
         (filter
          #(not= (read-string (get % "COUNTY")) 0)
          population-csv))))

(defn reset-database "Resets the database.  NOTE: This will destroy any open connections" []
  (d/delete-database db-uri)
  (d/create-database db-uri)
  (swap! conn (fn [n] (d/connect db-uri)))
  @(d/transact @conn schema-tx)
)

(defn reload-database "Reloads the database.  Connection must be reopened before running." []
    (transact-states)
    (transact-counties))


;;(buildState (first (read-csv)))
;;(get (buildState (first (read-csv))) :db/id)
;;(d/tempid :db.part/user)
;;(buildCounty (nth (read-csv) 225))
;;(get (buildCounty (second (read-csv))) :db/id)
;;(map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv)))
