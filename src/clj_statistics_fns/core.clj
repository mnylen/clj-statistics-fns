(ns clj-statistics-fns.core
  (:require [clj-statistics-fns.sorted-values :as v]))

(defn kth-percentile [k vs]
  (let [values (v/wrap vs)]
    (cond
      (= 0 (count values)) (throw (IllegalArgumentException. "kth-percentile on empty set is not supported"))
      (<= k 0) (throw (IllegalArgumentException. "k must be in range ]0, 100["))
      (>= k 100) (throw (IllegalArgumentException. "k must be in range ]0, 100["))
      :else (let [index (* (/ k 100) (count values))]
              (if (= 0 (mod index 1))
                (let [adjacent-sum (+ (nth values (dec index)) (nth values index))]
                  (/ adjacent-sum 2))
                (nth values (Math/floor index)))))))

(defn median [vs]
  (kth-percentile 50 vs))

(defn average [vs]
  (let [values (v/wrap vs)
        total-sum (reduce + (map #(* (first %) (second %)) (v/frequency-map values)))
        value-count (count values)]
    (if (= 0 value-count)
      (throw (IllegalArgumentException. "average on empty set is not supported"))
      (/ total-sum value-count))))

(defn minimum [vs]
  (-> vs
      v/wrap
      v/distinct-values
      first))

(defn maximum [vs]
  (-> vs
      v/wrap
      v/distinct-values
      last))
