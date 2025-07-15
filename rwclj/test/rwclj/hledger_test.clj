(ns rwclj.hledger-test
  (:require [clojure.test :refer :all]
            [rwclj.hledger :as hledger]
            [rwclj.db :as db]
            [ring.mock.request :as mock]
            [jsonista.core :as json])
  (:import [org.apache.jena.rdf.model ModelFactory]))

(def sample-journal
  "2024/02/20 Opening Balance
    assets:cash      $1000.00
    equity:opening-balances

2024/02/21 Grocery Store
    expenses:groceries   $50.00
    assets:cash")

(deftest hledger-import-test
  (testing "hledger journal import"
    (let [request (-> (mock/request :post "/api/hledger/import")
                      (mock/body sample-journal)
                      (mock/content-type "text/plain"))
          response (hledger/import-hledger-handler request)]
      (is (= 201 (:status response)))
      (let [response-body (json/read-value (:body response))]
        (is (= "success" (get response-body "status"))))

      ;; Verify that the data was stored
      (let [query "PREFIX fibo-fbc: <https://spec.edmcouncil.org/fibo/ontology/FBC/FunctionalEntities/FinancialBusinessAndCommerce/>
                     SELECT (COUNT(?tx) as ?count) WHERE { ?tx a fibo-fbc:Transaction . }"
            result (db/execute-sparql-select query)]
        (is (= "2" (-> result first :count)))))))
