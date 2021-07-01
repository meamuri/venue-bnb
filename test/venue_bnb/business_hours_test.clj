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
