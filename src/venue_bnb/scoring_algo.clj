(ns venue-bnb.scoring-algo
  (:require [venue-bnb.scoring.validation :refer [validate]]))

(defn- compare-venues [a b]
  -1)

(defn ^{:added "0.1.0"} score
  "Scores and sorts 'popular' venues.
   ## Params
    * `items` - list of venues"
  [items]
  (validate items)
  (let [sort (partial sort-by :score)]
    (->> items
         (sort compare-venues)
         (map #(dissoc % :score))
         reverse)))
