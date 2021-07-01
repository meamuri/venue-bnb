(ns venue-bnb.business-hours
  ""
  (:import java.util.Date
           java.time.Clock
           java.time.DayOfWeek
           java.time.Instant
           java.time.LocalTime
           java.time.LocalDateTime
           java.time.ZoneOffset))

(defn- date->local-date-time
  ""
  [date]
  (LocalDateTime/ofInstant (.toInstant date) ZoneOffset/UTC))

(defn- days
  ""
  [^java.util.Date start]
  (let [start* (date->local-date-time start)]
    (iterate #(.plusDays % 1) start*)))

(defn- days-range
  ""
  ([^Date start]
   (days-range start (Date/from (Instant/now))))
  ([^Date start ^Date end]
   (take-while #(.isBefore % end) (days start))))

(defn- scedule-entity->date
  ""
  [schedule-entity])

(defn- schedule->day-of-weeks-to-hours
  ""
  [acc curr]
  (let [day-of-week (DayOfWeek/from 3)]))

(defn- flattent-schedule
  ""
  [schedule]
  (->> schedule
       (map (fn [{:schedule/keys [weekdays hours]}]
              (mapv (fn [e]
                      [e hours]) weekdays)))
       (mapcat identity)
       (map (fn [[k v]]
              [(DayOfWeek/of k) v]))
       (into {})))

(defn calculate-business-hours
  ""
  [schedule start-date end-date]
  (let [days (days-range start-date end-date)
        day-of-week-to-hours (reduce schedule->day-of-weeks-to-hours {} schedule)]))

(defn calculate-date
  ""
  [schedule from business-hours])
