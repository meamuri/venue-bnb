(ns venue-bnb.business-hours
  ""
  (:import java.util.Date
           java.time.Clock
           java.time.LocalDateTime
           java.time.ZoneOffset))


(defn- days
  ""
  [^java.util.Date start]
  (let [start* (LocalDateTime/ofInstant (.toInstant start) ZoneOffset/UTC)]
    (iterate #(.plusDays % 1) start*)))

(defn- till-now
  ""
  [^Date start]
  (let [now (LocalDateTime/now (Clock/systemUTC))]
   (take-while #(.isBefore % now) (days start))))

(defn- scedule-entity->date
  ""
  [schedule-entity])

(defn calculate-business-hours
  ""
  [schedule start-date end-date])

(defn calculate-date
  ""
  [schedule from business-hours])
