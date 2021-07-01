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
