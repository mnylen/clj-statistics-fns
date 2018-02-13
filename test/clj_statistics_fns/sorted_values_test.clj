(ns clj-statistics-fns.sorted-values-test
  (:require [clojure.test :refer :all]
            [clj-statistics-fns.sorted-values :as v]))

(deftest test-sorted-values
  (let [test-values [5 1 2 3 1 4 5]
        sorted (apply v/sorted-values test-values)]
    (testing "count"
      (is (= (count sorted) (count test-values))
          "Expected count to be the count of input values"))

    (testing "add-value"
      (is (= (-> sorted (v/add-value 1) (v/add-value 2) (v/add-value 6))
             (apply v/sorted-values (concat test-values [1 2 6])))))

    (testing "distinct-values"
      (is (= '(1 2 3 4 5) (v/distinct-values sorted))
          "distinct-values returned non-unique values"))

    (testing "nth"
      (is (= (sort test-values) (map #(nth sorted %) (range (count sorted))))
          "nth returned wrong values")
      (is (nil? (nth sorted (inc (count sorted))))
          "nth didn't return nil when going out of bounds"))

    (testing "frequency-map"
      (is (= {1 2, 2 1, 3 1, 4 1, 5 2} (v/frequency-map sorted))
          "frequency-map was wrong"))

    (testing "frequency"
      (doseq [[v cnt] (v/frequency-map sorted)]
        (is (= cnt (v/frequency sorted v))
            (str "frequency of " v " did not match expected: " cnt))))

    (testing "equals"
      (is (true? (= (v/sorted-values 1 2 3) (v/sorted-values 3 2 1))))
      (is (false? (= (v/sorted-values 1 1 2) (v/sorted-values 1 2))))
      (is (false? (= (v/sorted-values 1 2 3) [1 2 3]))))

    (testing "hashCode"
      (is (= (.hashCode (v/frequency-map (v/sorted-values 1 2 3)))
             (.hashCode (v/sorted-values 1 2 3)))))

    (testing "toString"
      (is (= "SortedValues{}" (str (v/sorted-values))))
      (is (= "SortedValues{1 1, 2 1, 3 1}" (str (v/sorted-values 1 2 3)))))

    (testing "wrap"
      (is (= (v/sorted-values 1 2 3) (v/wrap [1 2 3])))
      (is (= sorted (v/wrap sorted))))))
