(ns rwclj.db-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.java.io :as io])
  (:import [org.apache.jena.tdb2 TDB2Factory]
           [org.apache.jena.query Dataset]))

(def test-db-dir-str "target/test-jena-db")

(defn- ensure-empty-dir! [dir-str]
  (let [dir-file (io/file dir-str)]
    (when (.exists dir-file)
      (doseq [f (reverse (file-seq dir-file))]
        (when (.exists f)
          (try
            (io/delete-file f)
            (catch Exception _)))))
    (.mkdirs dir-file)))

(defn setup-test []
  (ensure-empty-dir! test-db-dir-str))

(defn get-test-dataset []
  (TDB2Factory/connectDataset test-db-dir-str))

(use-fixtures :each (fn [f]
                      (setup-test)
                      (f)
                      (setup-test)))

(deftest get-dataset-test
  (testing "Dataset creation"
    (let [dataset (get-test-dataset)]
      (try
        (is (instance? Dataset dataset) "Should return a Dataset instance")
        (finally
          (.close dataset))))))

(deftest dataset-model-test
  (testing "Getting default model from dataset"
    (let [dataset (get-test-dataset)]
      (try
        (let [model (.getDefaultModel dataset)]
          (is (not (nil? model)) "Should get a non-nil model"))
        (finally
          (.close dataset))))))