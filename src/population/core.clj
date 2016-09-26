(ns population.core
  (:gen-class))
(use 'population.build-database)
(use 'clojure.pprint)

(defn rebuild-database []
  (reset-database)
  (reload-database)
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
