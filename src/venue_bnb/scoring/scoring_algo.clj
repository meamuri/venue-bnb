(ns venue-bnb.scoring.scoring-algo
  {:author "Roman Dronov"}
  (:require [venue-bnb.scoring.validation :refer [validate]]))

(defn- reviews-compare
  [lft rgt]
  (let [{l-rating :rating
         l-reviews :reviews} lft
        {r-rating :rating
         r-reviews :reviews} rgt
        ratio (max (/ l-reviews r-reviews) (/ r-reviews l-reviews))]
    (if (> ratio 4)
      ;; if difference between reviews is significant, just return reviews winner
      ;; in this case rating difference doesn't matter
      (comp l-reviews r-reviews)
      ;; here reviews ration check
      (if (and (not= l-rating r-rating)
               (> (Math/abs (- l-rating r-rating)) ratio))
        ;; if difference betweeb rating more than
        (comp l-rating r-rating)
        ;; if rating is comparable let's use anouther metrics: we can't make decision
        nil))))

(defn- booking-compare
  [lft rgt]
  (let [{l-booking-requests :booking-requests
         l-reservations :reservations} lft
        {r-booking-requests :booking-requests
         r-reservations :reservations} rgt]
    (if (not= l-reservations r-reservations)
      (comp l-reservations r-reservations)
      (if (not= l-booking-requests r-booking-requests)
        (comp l-booking-requests r-booking-requests)
        nil))))

(defn- compare-venues
  [lft rgt]
  ;; must significant criteria: by rating.
  (if-let [reviews-ration (reviews-compare lft rgt)]
    reviews-ration
    ;; if rating almost similar, go to check by booking ration
    (if-let [booking-ratio (booking-compare lft rgt)]
      booking-ratio
      ;; if entities almost similar by previous criterias
      ;; just sort by name ))
      (comp (:venue lft) (:venue rgt)))))

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
