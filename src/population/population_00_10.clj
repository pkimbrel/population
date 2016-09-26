(ns population.population-00-10)
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])

(def db-uri "datomic:dev://localhost:4334/population")
(def conn (atom (d/connect db-uri)))

(def population-csv (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

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
  (let [state (format "%02d" (read-string (get record "STATE")))
        county (format "%03d" (read-string (get record "COUNTY")))
        geoid (str "g" state county)]
    {:db/id (d/tempid :db.part/user)
     :map.population/map.county (get-county-id geoid)
     :map.population/year year
     :map.population/amount (read-string
                             (get record (str "POPESTIMATE" year)))
     }))

(defn process-year [year]
  (map
   #(build-population % year)
   (filter #(not= (read-string (get % "COUNTY")) 0)
           population-csv)))

(defn transact-year [year]
  @(d/transact @conn (process-year year)))

(defn transact-all []
  (swap! conn (fn [n] (d/connect db-uri)))
  (map transact-year (range 2000 2011 1)))

;;(range 2000 2010 1)
;;(process-year 2000)
;;(transact-all)

;; @(d/transact conn (map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))

