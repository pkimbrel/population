(ns population.population-10-15)
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])

(def db-uri "datomic:dev://localhost:4334/population")
(def conn (atom (d/connect db-uri)))

(def population10-csv (parse-csv (slurp "resources/PEP_2015_PEPANNRES_with_ann.csv")))

(defn get-county-entity [geoid]
  (d/q '[:find ?e
         :in $ [?geoid]
         :where
         [?e :map.county/geoid ?geoid]
        ]
       (d/db @conn)
       [geoid])
  )

(defn get-county-id [geoid]
  (first (first (get-county-entity geoid)))
  )

(defn build-population [record year]
  (let [geoid (str "g" (get record "GEO.id2"))]
    {:db/id (d/tempid :db.part/user)
     :map.population/map.county (get-county-id geoid)
     :map.population/year year
     :map.population/amount (read-string (get record (str "respop7" year)))
     }))

(defn process-year [year]
  (filter (comp some? :map.population/map.county) (map
              #(build-population % year)
              population10-csv)))

(defn transact-year [year]
  @(d/transact @conn (process-year year)))

(defn transact-all []
  (swap! conn (fn [n] (d/connect db-uri)))
  (map transact-year (range 2011 2016 1)))

;;(range 2011 2016 1)

;;(process-year 2011)
;;(spit "resources/temp.edn" (with-out-str (pr (map process-year (range 2011 2016 1)))))


;;(transact-all)

;; @(d/transact conn (map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))

