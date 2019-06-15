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
     (catch Exception e
       (prn (:cause e))))

(try (cql/create-keyspace
      session keyspace
      (dsl/with {:replication
                 {"class"              "SimpleStrategy"
                  "replication_factor" 1}}))
     (catch Exception e
       (prn (:cause e))))

(try (cql/drop-table
      session
      :users)
     (catch Exception e
       (prn (:cause e))))

(try (cql/create-table
      session
      :users
      (dsl/column-definitions {:name        :varchar
                               :age         :int
                               :city        :varchar
                               :primary-key [:name]}))
     (catch Exception e
       (prn (:cause e))))


(cql/insert session
            :users
            {:name "Alex"
             :city "Munich"
             :age  (int 19)})

(prn (cql/select session
                 :users))