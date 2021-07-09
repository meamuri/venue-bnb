# Usage

## Task 1

Run `lein repl` and type:

```clojure
(-> (slurp "./sample-data.edn")
    edn/read-string
    scoring/score-with-math)
```

Make sure that you have expected `"./sample-data.edn"` test file
and that you launch code from `user` namespace.
If you are in another namespace, please, execute before testing:
```clojure
(require '[venue-bnb.scoring :as scoring]
         '[clojure.edn :as edn])
```

## Task 2 & 3

Just use tests in `test/venue_bnb/business_hours_test.clj`.
Change any test inputs and use `lein kaocha` for running tests.

# Eventum technical exercise

## Task 1: Scoring and sorting

### Prerequisites

Sample data

### Description

In the sample data we have a vector of maps with the following specification

Key | Type | Description
----| ---- | -----------
:venue | String | Unique venue ID
:booking-requests | Long | Total number of booking requests received
:reservations | Long | Total number of actual reservations
:reviews | Long | Total number of customer reviews
:rating | Double | Average customer review rating

### Objective

Propose an algorithm for scoring and sorting based on “most popular” venue.

The algorithm should consider:

- It’s better to have 10 reviews with an average score of 4 vs. 2 reviews with an average score of 5
- It’s better to have 100 booking requests and 25 reservations vs. 10 booking requests and 5 reservations
- It’s better to have 30 booking requests and 10 reservations vs. 50 booking requests and 10 reservations.
- It’s better to have 20 booking requests and 5 reservations vs. 50 booking requests and 5 reservations

## Task 2: Date and time calculations

### Description

Each venue has different opening hours for their booking office.

Basically a time window for when they are available to handle incoming booking requests and customer enquiries.

Most of them have a simple time window like monday-friday 09:00 to 17:00, but some may have more complex schedules like monday-wednesday 09-17 and thursday-friday 09-15 and some may even have schedules like monday-wednesday 09-11, 14-17 and thursday-friday: closed.

Each schedule is represented as a map with the following specification

Key | Type | Description
----| ---- | -----------
:schedule/weekdays| set of longs (1-7) | Weekdays schedule is valid (1=monday, 7=sunday)
:schedule/hours | set of longs (0-24) | Hours schedule is valid

Venues may have multiple schedules.

#### Examples

##### Monday - friday 09-17

```clojure
[{:schedule/weekdays #{1 2 3 4 5}
  :schedule/hours #{9 10 11 12 13 14 15 16}}]
```

##### Monday - wednesday 09-17, thursday - friday 09-15

```clojure
[{:schedule/weekdays #{1 2 3}
  :schedule/hours #{9 10 11 12 13 14 15 16}}

 {:schedule/weekdays #{4 5}
  :schedule/hours #{9 10 11 12 13 14}}]
```

##### Monday - wednesday 09-11, 14-17 and thursday - friday: closed

```clojure
[{:schedule/weekdays #{1 2 3}
  :schedule/hours #{9 10}}

 {:schedule/weekdays #{1 2 3}
  :schedule/hours #{14 15 16}}]
```

### Objective

#### Implement a function that calculates the number of business hours that has elapsed between two dates


Function signature:

```clojure
(defn calculate-business-hours
  [schedule start-date end-date])
```

Examples

```clojure
(let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                 :schedule/hours #{9 10 11 12 13 14 15 16}}]]
  (calculate-business-hours schedule #inst "2018-03-02T07:00" #inst "2018-03-04T07:00"))
;; => 8

(let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                 :schedule/hours #{9 10 11 12 13 14 15 16}}]]
  (calculate-business-hours schedule #inst "2018-03-02T07:00" #inst "2018-03-05T12:00"))
;; => 11
```

Please ignore any timezone related issues. Just assume everything is UTC

## Task 3: Date and time calculations 2

### Description

This is an extension of `Task 2` with a slightly different use case.

Instead of calculating the number of business hours that has elapsed, we want to know what date (which must be a valid "business hour") a given input of schedule, start-date and X business hours falls on.


### Objective

Implement a function `calculate-date` with the following signature

```clojure
(defn calculate-date
  [schedule from business-hours])
```

#### Parameters

- `schedule`  Same specification as in task 2
- `from` Any valid #inst
- `business-hours` Number of business hours (long)

#### Return value
 
 `#inst` that falls within given schedule after X business hours has elapsed


### Examples

```clojure

(let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                 :schedule/hours #{9 10 11 12 13 14 15 16}}]]
  (calculate-date schedule #inst"2018-03-16T01:00:00" 1))
=> #inst"2018-03-16T10:00:00.000-00:00"

(let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                 :schedule/hours #{9 10 11 12 13 14 15 16}}]]
  (calculate-date schedule #inst"2018-03-16T01:00:00" 16))
=> #inst"2018-03-20T09:00:00.000-00:00"

(let [schedule [{:schedule/weekdays #{1 2 3 4 5}
                 :schedule/hours #{16}}]]
  (calculate-date schedule #inst"2018-03-16T01:00:00" 8))
=> #inst"2018-03-28T16:00:00.000-00:00"

(let [schedule [{:schedule/weekdays #{5}
                 :schedule/hours #{9 10 11 12 13 14 15 16}}                
                {:schedule/weekdays #{4}
                 :schedule/hours #{17}}]]
  (calculate-date schedule #inst"2018-03-16T01:00:00" 8))
=> #inst"2018-03-22T17:00:00.000-00:00"  
```
