(ns cassaforte-playground.core
  (:require [clojure.string :as str]))

(defn main [& x]
  (println (str/join " " x)))
