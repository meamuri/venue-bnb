(ns venue-bnb.business-hours
  ""
  (:import java.util.Date
           java.time.Clock
           java.time.Instant
           java.time.LocalTime
           java.time.LocalDateTime
           java.time.ZoneOffset))

(defn- date->local-date-time [date]
  (LocalDateTime/ofInstant (.toInstant date) ZoneOffset/UTC))

(defn- days
  ""
  [^java.util.Date start]
  (let [start* (date->local-date-time start)]
    (iterate #(.plusDays % 1) start*)))

(defn- days-until
  ""
  ([^Date start]
   (days-until start (Date/from (Instant/now))))
  ([^Date start ^Date end]
   (take-while #(.isBefore % end) (days start))))

(defn- scedule-entity->date
  ""
  [schedule-entity])

(defn calculate-business-hours
  ""
  [schedule start-date end-date])

(defn calculate-date
  ""
  [schedule from business-hours])
