(ns population.core
  (:gen-class)
  (:require [datomic.api :only [q db] :as d]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [population.config :as config]
            [population.build-database :as db]
            [population.population-90-00 :as pop00]
            [population.population-00-10 :as pop10]
            [population.population-10-15 :as pop15]
            [population.handler :as handler]))

(defn rebuild-database []
  (db/reset-database)
  (db/build-database))

(defn process-population []
  (let [db (d/db (d/connect config/db-uri))]
    (concat
     (pop00/process-years db)
     (pop10/process-years db)
     (pop15/process-years db))))

(defn transact [datum]
  @(d/transact (d/connect config/db-uri) datum))

(defn load-population []
  (let [db (d/db (d/connect config/db-uri))]
    (do
      ;;(apply #(transact %) (vector (pop00/process-year db 90)))
      ;;(apply #(transact %) (vector (pop00/process-year db 91)))
      ;;(apply #(transact %) (vector (pop00/process-year db 92)))
      ;;(apply #(transact %) (vector (pop00/process-year db 93)))
      ;;(apply #(transact %) (vector (pop00/process-year db 94)))
      ;;(apply #(transact %) (vector (pop00/process-year db 95)))
      ;;(apply #(transact %) (vector (pop00/process-year db 96)))
      ;;(apply #(transact %) (vector (pop00/process-year db 97)))
      ;;(apply #(transact %) (vector (pop00/process-year db 98)))
      ;;(apply #(transact %) (vector (pop00/process-year db 99)))
      (apply #(transact %) (vector (pop10/process-years db)))
      (apply #(transact %) (vector (pop15/process-years db)))
      )))

;;(rebuild-database)
;;(load-population)














;;(pop10/process-years (d/db (d/connect db-uri)))
;;(spit "temp.edn" (with-out-str (pr (process-population))))






