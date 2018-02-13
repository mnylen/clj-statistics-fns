(ns clj-statistics-fns.sorted-values)

(defprotocol ISortedValues
  (distinct-values [this])
  (add-value [this v])
  (frequency [this v])
  (frequency-map [this]))

(deftype SortedValues [value-frequencies]
  ISortedValues
  (frequency-map [this]
    value-frequencies)

  (frequency [this v]
    (get value-frequencies v 0))

  (distinct-values [this]
    (sort (keys value-frequencies)))

  (add-value [this v]
    (SortedValues. (merge-with + value-frequencies {v 1})))

  clojure.lang.Indexed
  (nth [this i]
    (loop [values (distinct-values this)
           start-index 0]
      (if (empty? values)
        nil
        (let [current-value (first values)
              value-frequency (get value-frequencies current-value)
              end-index (+ start-index value-frequency)]
          (if (< i end-index)
            current-value
            (recur (rest values) end-index))))))

  clojure.lang.Counted
  (count [this]
    (reduce + (vals value-frequencies)))

  java.lang.Object
  (toString [this]
    (str "SortedValues" value-frequencies ""))

  (hashCode [this]
    (.hashCode value-frequencies))

  (equals [this other]
    (and (instance? SortedValues other)
         (= value-frequencies (frequency-map other)))))

(defn sorted-values [& values]
  (SortedValues. (frequencies values)))

(defn wrap [values]
  (if (instance? SortedValues values)
    values
    (apply sorted-values values)))
