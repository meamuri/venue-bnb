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
  [^Date date]
  (LocalDateTime/ofInstant (.toInstant date) ZoneOffset/UTC))

(defn- days
  ""
  [^LocalDateTime start]
  (iterate #(.plusDays % 1) start))

(defn- days-range
  ""
  ([^LocalDateTime start]
   (days-range start (LocalDateTime/now ZoneOffset/UTC)))
  ([^LocalDateTime start ^LocalDateTime end]
   (take-while #(.isBefore % end) (days start))))

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

(defn- processor
  ""
  [schedule]
  (fn [{:keys [day-of-week from to]}]
    (let [hours (get schedule day-of-week)]
      (->> hours
           (filter #(and (> % from) (< % to)))  ;; TODO: fix it, it is'not identity!
           count))))

(defn- day->internal-representation
  ""
  [^LocalDateTime start ^LocalDateTime to]
  (fn [^LocalDateTime day]
    (let [date (.toLocalDate day)]
      {:day-of-week (.getDayOfWeek day)
       :from (if (= (.toLocalDate start) date) (.getHour start) 0)
       :to (if (= (.toLocalDate to) date) (.getHour to) 24)})))

(defn calculate-business-hours
  ""
  [schedule ^Date start-date ^Date end-date]
  (let [start (date->local-date-time start-date)
        to (date->local-date-time end-date)
        days (days-range start to)
        prepared-days (map (day->internal-representation start to) days)
        schedule' (flattent-schedule schedule)
        hours-per-day (map (processor schedule') prepared-days)]
    (reduce + 0 hours-per-day)))

(defn calculate-date
  ""
  [schedule from business-hours])
