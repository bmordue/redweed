(ns rwclj.review-test
  (:require [clojure.test :refer :all]
            [rwclj.review :as review]
            [rwclj.db :as db]))

(deftest import-review-test
  (testing "Test importing a review"
    (let [review-data {:rating 5 :text "Great product!"}
          result (review/import-review review-data)]
      (is (= "success" (:status result)))
      (is (not (nil? (:review-uri result)))))))
