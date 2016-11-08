(ns population.core
  (:gen-class)
  (:require [datomic.api :only [q db] :as d]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [population.config :as config]
            [population.build-database :as db]
            [population.population-00-10 :as pop10]
            [population.population-10-15 :as pop15]
            [population.handler :as handler]))

(defn rebuild-database []
  (db/reset-database)
  (db/build-database))

(defn process-population []
  (let [db (d/db (d/connect config/db-uri))]
    (concat
     (pop10/process-years db)
     (pop15/process-years db))))

(defn transact [datum]
  @(d/transact (d/connect config/db-uri) datum))

(defn load-population []
  (apply #(transact %) (vector (process-population))))

(defroutes app-routes
  (GET "/" [] "<h1>Hello World</h1>")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (wrap-defaults app-routes site-defaults))

(comment defn -main [& args]
  (jetty/run-jetty app {:port 9080}))

;; (-main)


;;(rebuild-database)


;;(process-population)

;;(load-population)







;;(pop10/process-years (d/db (d/connect db-uri)))
;;(spit "temp.edn" (with-out-str (pr (process-population))))






