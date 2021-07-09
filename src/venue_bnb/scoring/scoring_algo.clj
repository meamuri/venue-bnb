(ns venue-bnb.scoring.scoring-algo
  {:author "Roman Dronov"}
  (:require [venue-bnb.scoring.validation :refer [validate]]))

(defn safety-ration
  [lft rgt]
  (if (and (nil? lft) (nil? rgt))
    nil
    (if (or (and (nil? lft)
                 (some? rgt))
            (and (some? lft)
                 (nil? rgt)))
      ##Inf
      (max (/ lft rgt) (/ rgt lft)))))

(defn- careful-compare
  [lft rgt]
  (let [res (compare lft rgt)]
    (when (not= 0 res)
      res)))

(defn- reviews-compare
  [{l-rating :rating
    l-reviews :reviews}
   {r-rating :rating
    r-reviews :reviews}]
  (if (= l-rating r-rating)
    (careful-compare l-reviews r-reviews)
    (let [ratio (safety-ration l-reviews r-reviews)]
      (if (or (nil? ratio) (= ##Inf ratio))
        (careful-compare l-rating r-rating)
        (if (> ratio 4)
          ;; if difference between reviews is significant, just return reviews winner
          ;; in this case rating difference doesn't matter
          (careful-compare l-reviews r-reviews)
          ;; here reviews ration check
          (if (> (Math/abs (- l-rating r-rating)) ratio)
            ;; if difference betweeb rating more than
            (careful-compare l-rating r-rating)
            ;; if rating is comparable let's use anouther metrics: we can't make decision
            nil))))))

(defn- booking-compare
  [{l-booking-requests :booking-requests
    l-reservations :reservations}
   {r-booking-requests :booking-requests
    r-reservations :reservations}]
  (if-let [r (careful-compare l-reservations r-reservations)]
    r
    (if-let [r (careful-compare l-booking-requests r-booking-requests)]
      r
      nil)))

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
      (compare (:venue lft) (:venue rgt)))))

(defn ^{:added "0.1.0"} score
  "Scores and sorts 'popular' venues.
   ## Params
    * `items` - list of venues"
  [items]
  (validate items)
  (let [sort (partial sort-by :score)]
    (->> items
         (sort compare-venues)
         (map #(dissoc % :score)))))
