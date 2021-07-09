(ns venue-bnb.scoring.validation
  (:require [malli.core :as m]))

(def ^:private Item
  [:map
   [:venue string?]
   [:booking-requests {:optional true} [:or number? nil?]]
   [:reservations {:optional true} [:or number? nil?]]
   [:response-rate {:optional true} [:or number? nil?]]
   [:reviews {:optional true} [:or number? nil?]]
   [:rating {:optional true} [:or number? nil?]]])

(def ^:private Items
  [:* Item])

(defn validate
  [items]
  (when-not (m/validate Items items)
    (throw (ex-info "Illegal input" {}))))
