(ns rwclj.server-integration-test
  (:require
   [clj-http.client :as http]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [jsonista.core :as json]
   [rwclj.server :as server]
   [rwclj.db :as db]))

;; (def test-port 9876) ; Use a different port for testing
;; (def base-url (str "http://localhost:" test-port))
;; (def test-db-dir-str "target/test-jena-integration-db")

;; (defn- ensure-empty-dir! [dir-str]
;;   (let [dir-file (io/file dir-str)]
;;     (when (.exists dir-file)
;;       (doseq [f (reverse (file-seq dir-file))]
;;         (io/delete-file f)))
;;     (.mkdirs dir-file)))

;; (defonce test-server (atom nil))

;; (defn server-fixture [f]
;;   (ensure-empty-dir! test-db-dir-str)
;;   (let [dataset (db/get-dataset)]
;;     (try
;;       (reset! test-server (server/start-server! test-port))
;;       (f) ; Run the tests
;;     (finally
;;       (when @test-server
;;         (.stop @test-server) ; Assuming Jetty server object has a stop method
;;         (reset! test-server nil))
;;       (ensure-empty-dir! test-db-dir-str)
;;       (.close dataset))))) ; Restore original

;; (use-fixtures :once server-fixture) ; :once because server start/stop is expensive

(deftest ^:kaocha/skip health-check-test
  (testing "Health check endpoint"
    (let [response (http/get (str base-url "/health") {:throw-exceptions false})
          body (json/read-value (:body response))]
      (is (= 200 (:status response)))
      (is (= "ok" (:status body)))
      (is (= "redweed" (:service body))))))

(deftest ^:kaocha/skip api-docs-test
  (testing "API documentation endpoint"
    (let [response (http/get (str base-url "/api") {:throw-exceptions false})
          body (json/read-value (:body response))]
      (is (= 200 (:status response)))
      (is (vector? (:endpoints body)))
      (is (pos? (count (:endpoints body)))))))

(deftest ^:kaocha/skip vcard-import-integration-test
  (testing "vCard import via API"
    (let [sample-vcard-text "BEGIN:VCARD\nVERSION:3.0\nFN:Integration Test User\nN:User;Integration Test;;;\nEMAIL:integration@example.com\nEND:VCARD"
          expected-name "Integration Test User"]

      ;; Test with text/vcard
      (testing "Raw vCard import"
        (let [response (http/post (str base-url "/api/vcard/import")
                                  {:body sample-vcard-text
                                   :content-type "text/vcard"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response))]
          (is (= 201 (:status response)))
          (is (= "success" (:status response-body)))
          (is (string? (:person-uri response-body)))

          ;; Verify data in DB
          (let [person-uri (:person-uri response-body)
                query-results (db/execute-sparql-select
                               (db/get-dataset)
                               (str "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                                    "SELECT ?name WHERE { <" person-uri "> foaf:name ?name . }"))]
            (is (= 1 (count query-results)))
            (is (= expected-name (-> query-results first :name))))))

      ;; Clear DB for next sub-test (or rely on distinct URIs if not clearing)
      ;; For simplicity here, we'll assume distinct URIs or non-interference.
      ;; A more robust test might clean between sub-tests or use specific identifiers.
      (ensure-empty-dir! test-db-dir-str) ; Clean for the next one

      ;; Test with application/json
      (testing "JSON vCard import"
        (let [json-payload {:vcard sample-vcard-text}
              response (http/post (str base-url "/api/vcard/import")
                                  {:body (json/write-value-as-string json-payload)
                                   :content-type "application/json"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response))]
          (is (= 201 (:status response)))
          (is (= "success" (:status response-body)))
          (is (string? (:person-uri response-body)))

          ;; Verify data in DB
          (let [person-uri (:person-uri response-body)
                query-results (db/execute-sparql-select
                               (db/get-dataset)
                               (str "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                                    "SELECT ?name WHERE { <" person-uri "> foaf:name ?name . }"))]
            (is (= 1 (count query-results)))
            (is (= expected-name (-> query-results first :name))))))

      (ensure-empty-dir! test-db-dir-str) ; Clean for the next one

      (testing "Invalid vCard import"
        (let [invalid-vcard "BEGIN:VCARD\nFN:Bad\nEND:VCARD" ; Missing VERSION
              response (http/post (str base-url "/api/vcard/import")
                                  {:body invalid-vcard
                                   :content-type "text/vcard"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response))]
          (is (= 400 (:status response)))
          (is (= "error" (:status response-body)))
          (is (str/includes? (str/lower-case (:message response-body)) "invalid vcard format"))))

      (testing "Unsupported content type"
        (let [response (http/post (str base-url "/api/vcard/import")
                                  {:body "<xml/>"
                                   :content-type "application/xml"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response))]
          (is (= 415 (:status response)))
          (is (= "error" (:status response-body)))
          (is (str/includes? (str/lower-case (:message response-body)) "unsupported content type")))))))

(comment
  ;; To run these tests from REPL (ensure server is not already running on test-port)
  ;; (clojure.test/run-tests 'rwclj.redweed.server-integration-test)
)
