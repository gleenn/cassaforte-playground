(ns cassaforte-playground.core
  (:require [clojure.string :as str]
            [clojurewerkz.cassaforte.client :as client]
            [clojurewerkz.cassaforte.cql :as cql]
            [clojurewerkz.cassaforte.query.dsl :as dsl]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;             MUST USE CASSANDRA 2.2!!!!!!!!           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def keyspace "glenn")

(def session (doto (client/connect ["127.0.0.1"] {:keyspace         keyspace
                                                  :protocol-version 2})
               (cql/use-keyspace keyspace)))

(defn main [& x]
  (println (str/join " " x)))

(try (cql/drop-keyspace
      session
      keyspace)
     (println "Dropped keyspace" keyspace)
     (catch Exception e
       (prn e)))

(try (cql/create-keyspace
      session keyspace
      (dsl/with {:replication
                 {"class"              "SimpleStrategy"
                  "replication_factor" 3}}))
     (println "Created keyspace" keyspace)
     (catch Exception e
       (prn e)))

(try (cql/drop-table
      session
      :users)
     (println "Dropped table" :users)
     (catch Exception e
       (prn e)))

(try (cql/create-table
      session
      :users
      (dsl/column-definitions {:name        :varchar
                               :age         :int
                               :city        :varchar

                               :primary-key [:name]}))
     (println "Created table" :users)
     (catch Exception e
       (prn e)))

(try (cql/create-index
      session
      :users
      :city
      (dsl/index-name :users_city)
      (dsl/if-not-exists))
     (println "Created index" :users :city)
     (catch Exception e
       (prn e)))

(cql/insert session
            :users
            {:name "Alex" :city "Munich" :age (int 19)})

(cql/insert-batch session
                  :users
                  [{:name "Glenn" :city "San Francisco" :age (int 13)}
                   {:name "Kristen" :city "San Francisco" :age (int 12)}])

;(prn (cql/select session :users))
;(prn (cql/select session :users (dsl/where [[= :name "Alex"]])))
; this will throw if you don't create the secondary index: Predicates on non-primary-key columns (city) are not yet supported for non secondary index queries
(prn (cql/select session :users (dsl/where [[= :city "San Francisco"]])))


;; This takes ~250-340ms to insert 10K rows! :D
#_(time
   (->> (range 10000)
        (map #(hash-map :name (str "User" %) :city "San Francisco" :age (int (mod % 100))))
        (partition-all 1000)
        (#(doseq [users %] (cql/insert-batch session :users users)))))