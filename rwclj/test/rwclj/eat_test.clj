(ns rwclj.eat-test
  (:require [clojure.test :refer :all]
            [rwclj.eat :as eat]
            [rwclj.db :as db]
            [rwclj.server :refer [app]]
            [jsonista.core :as json]
            [ring.mock.request :as mock])
  (:import [org.apache.jena.rdf.model ModelFactory]))

(deftest eat-handler-test
  (testing "POST /api/eat"
    (let [summary "I ate a delicious pizza"
          request (-> (mock/request :post "/api/eat")
                      (mock/header "Content-Type" "application/json")
                      (mock/body (json/write-value-as-string {:summary summary})))
          response (app request)
          body (json/read-value (:body response))]
      (is (= 201 (:status response)))
      (is (= "success" (get body "status")))
      (is (clojure.string/starts-with? (get body "activity-uri") "http://redweed.local/eat/")))))
