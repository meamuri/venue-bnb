(ns venue-bnb.scoring.scoring-math
  {:author "Roman Dronov"}
  (:require [venue-bnb.scoring.validation :refer [validate]]))

(defn- rating-weight [rating reviews]
  (+ (* rating 1.1) (* reviews 1.8)))

(defn- booking-weight [booking-rqeuest reservations]
  (+ (* booking-rqeuest 1.1) (* reservations 1.0)))

(defn- compute-score
  [{:keys [booking-requests reservations reviews rating]}]
  (let [rating-weight (rating-weight (or rating 0) (or reviews 0))
        booking-weight (booking-weight (or booking-requests 0) (or reservations 0))]
    (+ rating-weight booking-weight)))

(defn- normalize
  [e]
  (let [score (compute-score e)]
    (assoc e :score score)))

(defn compare-venues
  [left-venue right-venue]
  (let [score-res (compare (:score left-venue) (:score right-venue))]
    (if (not= 0 score-res)
      score-res
      (compare (:venue left-venue) (:venue right-venue)))))

(defn ^{:added "0.1.0"} score
  "Scores and sorts 'popular' venues.
   ## Params
    * `items` - list of venues"
  [items]
  (validate items)
  (->> items
       (map normalize)
       (sort compare-venues)
       (map #(dissoc % :score))
       reverse))
