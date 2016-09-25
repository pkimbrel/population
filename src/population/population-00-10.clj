(ns population.population-00-10
  (:gen-class))
(use 'csv-map.core)
(use '[datomic.api :only [q db] :as d])
(use 'clojure.pprint)

(def uri "datomic:dev://localhost:4334/population")

(def conn (d/connect uri))

(defn read-csv "Reads CSV" []
  (parse-csv (slurp "resources/CO-EST00INT-TOT.csv")))

(defn get-county-entity [fips]
  (d/q '[:find ?e
         :in $ [?fips]
         :where
         [?e :map.county/fips ?fips]
        ]
       (db conn)
       [fips])
  )

(defn get-county-id [fips]
  (first (first (get-county-entity fips)))
  )

(defn build-population [record year]
  {:db/id (d/tempid :db.part/user)
   :map.population/map.county (get-county-id (read-string (get record "COUNTY")))
   :map.population/amount (read-string (get record (str "POPESTIMATE" year)))
   }
  )


(defn process-year [year] (map #(build-population % year) (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))

(process-year 2000)
(defn process-years [] (map process-year (range 2000 2010 1)))

(process-years)

@(d/transact conn (process-years))
;; @(d/transact conn (map buildCounty (filter #(not= (read-string (get % "COUNTY")) 0) (read-csv))))

(comment d/transact conn [{:db/id #db/id[:db.part/user -1069389]
                    :map.population/map.county 17592186047714
                    :map.population/amount 44021}])


