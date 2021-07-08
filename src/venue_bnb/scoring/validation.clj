(ns venue-bnb.scoring.validation
  (:require [malli.core :as m]))

(def ^:private Item
  [:map
   [:venue string?]
   [:booking-requests number?]
   [:reservations number?]
   [:response-rate number?]
   [:reviews number?]
   [:rating number?]])

(def ^:private Items
  [:sequential Item])

(defn validate
  [items]
  (when (m/validate Items items)
    (throw (ex-info "Illegal input" {}))))
