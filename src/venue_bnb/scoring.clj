(ns venue-bnb.scoring
  "")

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
  ""
  [e]
  (let [score (compute-score e)]
    (assoc e :score score)))

(defn score
  [items]
  (let [sort (partial sort-by :score)]
    (->> items
         (map normalize)
         sort
         (map #(dissoc % :score))
         reverse)))
