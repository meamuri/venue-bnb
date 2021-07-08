(ns venue-bnb.business-hours-test
  (:require [venue-bnb.business-hours :as sut]
            [clojure.test :refer [deftest testing is]]))

(deftest business-hours
  (testing "days from 02.03.2018 to 04.03.2018 = 8 business hours"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-04T07:00"
          res (sut/calculate-business-hours schedule from to)]
      (is (= 8 res))))
  (testing "days from 02.03.2018 to 05.03.2018 = 11 business hours"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-05T12:00"
          res (sut/calculate-business-hours schedule from to)]
      (is (= 11 res)))))

(deftest business-hours-illegal-inputs
  (testing "start day is before after day"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-01T07:00"]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-business-hours schedule from to)))))
  (testing "Schedule contains 0 day"
    (let [schedule [{:schedule/weekdays #{0 1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-01T07:00"]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-business-hours schedule from to)))))
  (testing "Schedule contains 24 hour (23 is max, since range from 0 to 23)"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16 24}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-01T07:00"]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-business-hours schedule from to)))))
  (testing "Schedule doesn't contain weekdays field"
    (let [schedule [{:schedule/hours #{9 10 11 12 13 14 15 16 24}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-01T07:00"]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-business-hours schedule from to)))))
  (testing "Schedule doesn't contain hours field"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}}]
          from #inst "2018-03-02T07:00"
          to #inst "2018-03-01T07:00"]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-business-hours schedule from to))))))

(deftest calculate-date
  (testing "case 1"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          start #inst"2018-03-16T01:00:00"
          business-hours 1
          res (sut/calculate-date schedule start business-hours)]
      (is (= #inst"2018-03-16T10:00:00.000-00:00" res))))
  (testing "case 2"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          start #inst"2018-03-16T01:00:00"
          business-hours 16
          res (sut/calculate-date schedule start business-hours)]
      (is (= #inst"2018-03-20T09:00:00.000-00:00" res))))
  (testing "case 3"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{16}}]
          start #inst"2018-03-16T01:00:00"
          business-hours 8
          res (sut/calculate-date schedule start business-hours)]
      (is (= #inst"2018-03-28T16:00:00.000-00:00" res))))
  (testing "case 4"
    (let [schedule [{:schedule/weekdays #{5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}
                    {:schedule/weekdays #{4}
                     :schedule/hours #{17}}]
          start #inst"2018-03-16T01:00:00"
          business-hours 8
          res (sut/calculate-date schedule start business-hours)]
      (is (= #inst"2018-03-22T17:00:00.000-00:00" res)))))

(deftest calculate-date-invalid-input
  (testing "float point hours"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          start #inst"2018-03-16T01:00:00"
          business-hours 1.3]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-date schedule start business-hours)))))
  (testing "negative business hours"
    (let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                     :schedule/hours #{9 10 11 12 13 14 15 16}}]
          start #inst"2018-03-16T01:00:00"
          business-hours -5]
      (is (thrown-with-msg? RuntimeException #"Invalid input" (sut/calculate-date schedule start business-hours))))))
