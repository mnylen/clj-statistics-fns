(ns clj-statistics-fns.core-test
  (:require [clojure.test :refer :all]
            [clj-statistics-fns.core :as s]
            [clj-statistics-fns.sorted-values :as v]))

(deftest minimum-maximum-test
  (doseq [values-wrapper [identity v/wrap]]
    (testing "with no values"
      (is (= nil (s/minimum (values-wrapper []))))
      (is (= nil (s/maximum (values-wrapper [])))))

    (let [values (values-wrapper [50 60 70 40 20 80])]
      (is (= 20 (s/minimum values))
          "wrong minimum")
      (is (= 80 (s/maximum values))
          "wrong maximum"))))

(deftest median-test
  (doseq [values-wrapper [identity v/wrap]]
    (testing "with no values"
      (is (thrown? IllegalArgumentException (s/median (values-wrapper [])))))

    (testing "with only one value"
      (is (= 1 (s/median (values-wrapper [1])))))

    (testing "with odd number of values"
      (is (= 6 (s/median (values-wrapper [3 3 1 6 8 9 7])))))

    (testing "with even number of values"
      (is (= 9/2 (s/median (values-wrapper [3 3 1 6 8 9 2 7])))))))

(deftest average-test
  (testing "with no values"
    (is (thrown? IllegalArgumentException (s/average [])))
    (is (thrown? IllegalArgumentException (s/average (v/sorted-values)))))

  (testing "with only one value"
    (is (= 1 (s/average [1])))
    (is (= 1 (s/average (v/sorted-values 1)))))

  (testing "with multiple values"
    (is (= 7/4 (s/average [3 1 2 1])))
    (is (= 7/4 (s/average (v/sorted-values 3 1 2 1))))))

(deftest kth-percentile-test
  (doseq [values-wrapper [identity v/wrap]]
    (testing "with no values"
      (is (thrown? IllegalArgumentException (s/kth-percentile 95 (values-wrapper [])))))

    (testing "with invalid k"
      (let [values (values-wrapper (range 100))]
        (is (thrown? IllegalArgumentException (s/kth-percentile 0 values)))
        (is (thrown? IllegalArgumentException (s/kth-percentile 100 values)))
        (is (thrown? IllegalArgumentException (s/kth-percentile 110 values)))
        (is (thrown? IllegalArgumentException (s/kth-percentile -10 values)))))

    (testing "with only one value"
      (is (= 3 (s/kth-percentile 95 (values-wrapper [3])))))

    (testing "with multiple values"
      (let [values (values-wrapper [43 54 56 61 62 66 68 69 69 70 71 72 77 78 79 85 87 88 89 93 95 96 98 99 99])]
        (is (= 98 (s/kth-percentile 90 values)))
        (is (= 64 (s/kth-percentile 20 values)))
        (is (= 77 (s/kth-percentile 50 values) (s/median values)))
        (is (= 99 (s/kth-percentile 99 values)))))))
