(ns venue-bnb.business-hours
  ""
  (:import java.util.Date
           java.time.DayOfWeek
           java.time.LocalDateTime
           java.time.temporal.ChronoUnit
           java.time.ZoneOffset))

(defn- date->local-date-time
  ""
  [^Date date]
  (LocalDateTime/ofInstant (.toInstant date) ZoneOffset/UTC))

(defn- days
  ""
  [^LocalDateTime start]
  (iterate (fn [day]
             (-> day
               (.plusDays 1)
               (.withHour 0)
               (.truncatedTo ChronoUnit/HOURS))) start))

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
    (let [hours (get schedule day-of-week #{})]
      (->> hours
           (filter #(and (> % from) (< % to)))
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
    (apply + hours-per-day)))

(defn- compute-current-day
  [day hours-remainder schedule]
  (let [day-of-week (.getDayOfWeek day)
        day-schedule (get schedule day-of-week #{})
        business-hours (sort day-schedule)
        business-hours-of-current-day (count business-hours)]
    (if (> hours-remainder business-hours-of-current-day)
      (- hours-remainder business-hours-of-current-day)
      0)))

(defn- find-in-reminder
  [days schedule]
  (let [day (first days)
        day-of-week (.getDayOfWeek day)
        day-schedule (get schedule day-of-week #{})
        business-hours (sort day-schedule)]
    (if (seq business-hours)
      (-> day
          (.withHour (first business-hours))
          (.toInstant ZoneOffset/UTC)
          Date/from)
      (recur (rest days) schedule))))

(defn- compute-date
  ""
  [days hours-remainder schedule]
  (let [day (first days)
        day-of-week (.getDayOfWeek day)
        day-schedule (get schedule day-of-week #{})
        business-hours (sort day-schedule)
        h (-> (drop hours-remainder business-hours) first)]
    (if (some? h)
      (-> day
          (.withHour h)
          (.toInstant ZoneOffset/UTC)
          Date/from)
      (find-in-reminder (rest days) schedule))))

(defn calculate-date
  ""
  [schedule ^Date from business-hours]
  (let [days (days (date->local-date-time from))
        schedule' (flattent-schedule schedule)]
    (loop [h business-hours
           days' days]
      (let [d (first days')
            updated-h (compute-current-day d h schedule')]
        (if (= 0 updated-h)
          (compute-date days' h schedule')
          (recur updated-h (rest days')))))))
