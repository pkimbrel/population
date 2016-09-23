(require '[datomic.api :as d])

(def db-uri "datomic:dev://localhost:4334/population")
(d/delete-database db-uri)
(d/create-database db-uri)

(def conn (d/connect db-uri))

(def schema-tx (read-string (slurp "/Users/pkimbrel/Local/src/population/resources/population-schema.edn")))
;;(def coest00-tx (read-string (slurp "/Users/pkimbrel/Local/src/population/resources/population-coest00.edn")))

@(d/transact conn schema-tx)
;;@(d/transact conn coest00-tx)
