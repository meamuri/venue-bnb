(ns venue-bnb.business-hours.schema)

(defn- in-range [a b]
  (fn [x]
    (<= a x b)))

(def Schedule
  [:sequential
   [:map
    [:schedule/weekdays
     [:set
      [:and
       int?
       [:fn (in-range 1 7)]]]]
    [:schedule/hours
     [:set
      [:and
       int?
       [:fn (in-range 0 23)]]]]]])

(def BusinessHours
  [:and
   int?
   pos?])
