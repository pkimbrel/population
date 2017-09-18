(ns population.population-90-00
  (:require [clojure.string :as str]
            [datomic.api :only [q db] :as d]
            [population.config :as config]))

(defn slurp-data [year] (slurp (str "resources/STCH-icen19" year ".txt")))

(defn map-file [data]
    (map #(str/split % #" +") (str/split-lines data)))

(defn remap-file [data] (map #(hash-map (keyword (second %)) (read-string (get % 5))) data))

(defn reduce-population [data] )

(def demo-file (str "90 01001 0 0 0 10\n"
                    "90 01001 0 0 0 20\n"
                    "90 01001 0 0 0 30\n"
                    "90 02001 0 0 0 15\n"
                    "90 02001 0 0 0 15\n"
                    "90 02001 0 0 0 15\n"
                    ))

(defn reduce-population [data]
  (->> data
       (group-by #(keyword (second %)))
       (map #(do {(key %) (reduce + (map (fn [x] (read-string (get x 5))) (val %)))}))
       ))

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
  (let [geoid (str "g" (name (first (keys record))))
        population (first (vals record))
        state (subs geoid 1 3)
        county (subs geoid 3)]
    {:db/id (d/tempid :db.part/user)
     :map.population/map.county (get-county-id db geoid)
     :map.population/year (read-string (str "19" year))
     :map.population/amount population
     }))

(defn process-year [db year]
  (filter (comp some? :map.population/map.county)
          (map
           #(build-population db year %)
           (->> (slurp-data year)
                (map-file)
                (reduce-population)
                ))))

(defn process-years [db]
  (mapcat #(process-year db %) (range 90 100 1)))

;;(process-years (d/db (d/connect config/db-uri)))
;;(spit "stuff.txt" (with-out-str (pr (process-year (d/db (d/connect config/db-uri)) 90))))





;;(map #(do {(key %) (reduce + ((val %)))}) (group-by #(keyword (second %)) demo-file))
;;(group-by #(keyword (second %)) demo-file)
;;(map #(key %) (group-by second demo-file))

;;(map-file "a b c d\n1 2 3  4")

