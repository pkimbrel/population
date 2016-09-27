(ns population.population-10-15
    (:require [csv-map.core :as csv])
    (:require [datomic.api :only [q db] :as d]))

(def db-uri "datomic:dev://localhost:4334/population")
;;(def conn (atom (d/connect db-uri)))

(def population15-csv (csv/parse-csv (slurp "resources/PEP_2015_PEPANNRES_with_ann.csv")))

(defn get-county-entity [db geoid]
  (d/q '[:find ?e
         :in $ [?geoid]
         :where
         [?e :map.county/geoid ?geoid]
        ]
       db [geoid])
  )

(defn get-county-id [db geoid]
  (first (first (get-county-entity db geoid))))

(defn build-population [db year record]
  (let [geoid (str "g" (get record "GEO.id2"))]
    {:db/id (d/tempid :db.part/user)
     :map.population/map.county (get-county-id db geoid)
     :map.population/year year
     :map.population/amount (read-string (get record (str "respop7" year)))
      }))

(defn process-year [db year]
  (filter (comp some? :map.population/map.county)
          (map
           #(build-population db year %)
           population15-csv)))

(defn process-years [db]
  (mapcat #(process-year db %) (range 2011 2016 1)))

;;(process-years (d/db (d/connect db-uri)))


;;(defn transact-year [year]
;;  @(d/transact @conn (process-year year)))

;;(range 2011 2016 1)

;;(process-year 2011)
;;(spit "resources/temp.edn" (with-out-str (pr (map process-year (range 2011 2016 1)))))


;;(transact-all)

;; @(d/transact conn (map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))

