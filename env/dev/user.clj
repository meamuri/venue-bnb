(ns user
  (:require [venue-bnb.scoring :as scoring]
            [venue-bnb.scoring-algo :as algorithmic-scoring]
            [clojure.edn :as edn]))

(comment
  (-> (slurp "./sample-data.edn")
      edn/read-string
      scoring/score)

  (-> (slurp "./sample-data.edn")
      edn/read-string
      algorithmic-scoring/score))
