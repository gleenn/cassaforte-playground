(defproject cassaforte-playground "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clojurewerkz/cassaforte "3.0.0-alpha2-SNAPSHOT"]]
  :repl-options {:init-ns cassaforte-playground.core}
  :main cassaforte-playground.core/main)
