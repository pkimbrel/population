(ns population.population-00-10
    (:require [csv-map.core :as csv])
    (:require [datomic.api :only [q db] :as d]))

(def population10-csv (csv/parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn get-county-entity [db geoid]
  (d/q '[:find ?e
         :in $ [?geoid]
         :where
         [?e :map.county/geoid ?geoid]
         ]
       db [geoid])
  )

(defn get-county-id [db geoid]
  (first (first (get-county-entity db geoid)))
  )

(defn build-population [db year record]
  (let [state (format "%02d" (read-string (get record "STATE")))
        county (format "%03d" (read-string (get record "COUNTY")))
        geoid (str "g" state county)]
    {:db/id (d/tempid :db.part/user)
     :map.population/map.county (get-county-id db geoid)
     :map.population/year year
     :map.population/amount (read-string
                             (get record (str "POPESTIMATE" year)))
     }))

(defn process-year [db year]
  (map
   #(build-population db year %)
   (filter #(not= (read-string (get % "COUNTY")) 0)
           population10-csv)))

(defn process-years [db]
  (mapcat #(process-year db %) (range 2000 2011 1)))

;;(process-years (d/db (d/connect db-uri)))



;;(range 2000 2010 1)
;;(process-year 2000)
;;(transact-all)

;; @(d/transact conn (map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))

