(ns user
  (:require [venue-bnb.scoring :as scoring]
            [clojure.edn :as edn]))

(comment
  (-> (slurp "./sample-data.edn")
      edn/read-string
      scoring/score-with-math))
