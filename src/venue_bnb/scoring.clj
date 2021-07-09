(ns venue-bnb.scoring
  "Proposes an algorithm for scoring and sorting based on 'most popular' venue."
  {:author "Roman Dronov"}
  (:require [venue-bnb.scoring.scoring-math :as scoring-math]))

(def score-with-math scoring-math/score)
