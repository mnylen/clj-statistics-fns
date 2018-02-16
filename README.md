# clj-statistics-fns

[![Build Status](https://travis-ci.org/mnylen/clj-statistics-fns.svg?branch=master)](https://travis-ci.org/mnylen/clj-statistics-fns) [![Clojars Project](https://img.shields.io/clojars/v/mnylen/clj-statistics-fns.svg)](https://clojars.org/mnylen/clj-statistics-fns)

A simple Clojure library for calculating some common statistics of data sets:
minimum, maximum, average, median and k-th percentile.

## Usage

First, install by adding to your dependencies array:

    [mnylen/clj-statistics-fns "0.1.0"]

### Supported operations:

    (require '[clj-statistics-fns.core :as s])

    (s/average [1 2 3])
    ;; => 2

    (s/median [1 2 3])
    ;; => 2

    (s/kth-percentile 95 (range 0 101))
    ;; => 95

    (s/minimum [1 2 3])
    ;; => 1

    (s/maximum [1 2 3])
    ;; => 3

### Collecting values

All functions in the clj-statistics-fns.core namespace accept normal Clojure
collections, so you can just collect the values into a vector. However,
clj-statistics-fns also comes with an memory optimized data structure more suited
for collecting a lot of values falling within some finite range. If your data set has
a lot of duplicate values, you should use the optimized data structure instead.

Sample use & supported operations:

    (require '[clj-statistics-fns.sorted-values :as v])

    ;; create
    (v/sorted-values 1 2 3)
    ;; => #object[clj_statistics_fns.sorted_values.SortedValues 0x77b707a SortedValues{1 1, 2 1, 3 1}]

    (v/wrap [1 2 3])
    ;; => #object[clj_statistics_fns.sorted_values.SortedValues 0x56aa6084 SortedValues{1 1, 2 1, 3 1}]

    ;; add value
    (v/add-value (v/sorted-values 1 2 3) 2)
    ;; => #object[clj_statistics_fns.sorted_values.SortedValues 0x407d4167 SortedValues{1 1, 2 2, 3 1}]

    ;; get frequency of a value
    (v/frequency (v/sorted-values 3 2 1 2) 2)
    ;; => 2

    ;; count, nth
    (count (v/sorted-values 1  2 3))
    ;; => 3

    (nth (v/sorted-values 3 1 2) 0)
    ;; => 1

    (let [values (v/sorted-values 2 1 3 1 2 3)]
      (map #(nth values %) (range (count values))))
    ;; => (1 1 2 2 3 3)

With these, you could implement ring middleware for collecting response times:

    (require '[clj-statistics-fns.sorted-values :as v])

    ;; ring middleware for collecting response times:
    (def ^:private response-times
      (atom (v/sorted-values)))

    (defn collect-repsonse-times [handler]
      (fn [request]
        (let [start-time (System/currentTimeMillis)
              response (handler request)
              end-time (System/currentTimeMillis)
              response-time (- end-time start-time)]
          (swap! response-times v/add-value response-time)
          response))))

Then, somewhere else:

    (require '[clj-statistics-fns.core :as s])

    (s/median @response-times)
    (s/average @response-times)
    (s/kth-percentile 95 @response-times)

## License

Copyright © 2018 Mikko Nylén

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
