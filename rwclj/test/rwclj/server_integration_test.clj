(ns rwclj.server-integration-test
  (:require
   [clj-http.client :as http]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [jsonista.core :as json]
   [rwclj.server :as server]
   [rwclj.db :as db])
  (:import [org.apache.jena.tdb2 TDB2Factory]))

(def test-port 9876)
(def base-url (str "http://localhost:" test-port))
(def test-db-dir-str "target/test-jena-integration-db")

(defn- ensure-empty-dir! [dir-str]
  (let [dir-file (io/file dir-str)]
    (when (.exists dir-file)
      (doseq [f (reverse (file-seq dir-file))]
        (io/delete-file f)))
    (.mkdirs dir-file)))

(defonce test-server (atom nil))
(defonce test-dataset (atom nil))

(defn server-fixture [f]
  (ensure-empty-dir! test-db-dir-str)
  (let [dataset (TDB2Factory/connectDataset test-db-dir-str)]
    (reset! test-dataset dataset)
    (try
      (reset! test-server (server/start-server! test-port @test-dataset))
      (f)
      (finally
        (when @test-server
          (.stop @test-server)
          (reset! test-server nil))
        (when @test-dataset
          (.close @test-dataset)
          (reset! test-dataset nil))
        (ensure-empty-dir! test-db-dir-str)))))

(use-fixtures :once server-fixture)

(deftest health-check-test
  (testing "Health check endpoint"
    (let [response (http/get (str base-url "/health") {:throw-exceptions false})
          body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
      (is (= 200 (:status response)))
      (is (= "ok" (:status body)))
      (is (= "Redweed Server" (:service body))))))

(deftest vcard-import-integration-test
  (testing "vCard import via API"
    (let [sample-vcard-text "BEGIN:VCARD\nVERSION:3.0\nFN:Integration Test User\nN:User;Integration Test;;;\nEMAIL:integration@example.com\nEND:VCARD"
          expected-name "Integration Test User"]

      (testing "Raw vCard import"
        (let [response (http/post (str base-url "/api/import/vcard")
                                  {:body sample-vcard-text
                                   :content-type "text/vcard"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
          (is (= 201 (:status response)))
          (is (= "success" (:status response-body)))
          (is (string? (:person-uri response-body)))
          (let [person-uri (:person-uri response-body)
                query-results (db/execute-sparql-select
                               @test-dataset
                               (str "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                                    "SELECT ?name WHERE { <" person-uri "> foaf:name ?name . }"))]
            (is (= 1 (count query-results)))
            (is (= expected-name (:name (first query-results)))))))
      (ensure-empty-dir! test-db-dir-str)

      (testing "JSON vCard import"
        (let [json-payload {:vcard sample-vcard-text}
              response (http/post (str base-url "/api/import/vcard")
                                  {:body (json/write-value-as-string json-payload)
                                   :content-type "application/json"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
          (is (= 201 (:status response)))
          (is (= "success" (:status response-body)))
          (is (string? (:person-uri response-body)))
          (let [person-uri (:person-uri response-body)
                query-results (db/execute-sparql-select
                               @test-dataset
                               (str "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                                    "SELECT ?name WHERE { <" person-uri "> foaf:name ?name . }"))]
            (is (= 1 (count query-results)))
            (is (= expected-name (:name (first query-results)))))))
      (ensure-empty-dir! test-db-dir-str)

      (testing "Invalid vCard import"
        (let [invalid-vcard "BEGIN:VCARD\nFN:Bad\nEND:VCARD"
              response (http/post (str base-url "/api/import/vcard")
                                  {:body invalid-vcard
                                   :content-type "text/vcard"
                                   :throw-exceptions false})
              response-body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
          (is (= 400 (:status response)))
          (is (= "error" (:status response-body)))
          (is (str/includes? (str/lower-case (:message response-body)) "invalid vcard format")))))

    (testing "Unsupported content type"
      (let [response (http/post (str base-url "/api/import/vcard")
                                {:body "<xml/>"
                                 :content-type "application/xml"
                                 :throw-exceptions false})
            response-body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
        (is (= 415 (:status response)))
        (is (= "error" (:status response-body)))
        (is (str/includes? (str/lower-case (:message response-body)) "unsupported content type"))))))

(deftest photo-import-integration-test
  (testing "Photo import via API"
    (let [image-file (io/file "test/resources/test-image.jpg")]
      (testing "Successful photo upload"
        (let [response (http/post (str base-url "/api/import/photo")
                                  {:multipart [{:name "file" :content image-file}]
                                   :throw-exceptions false})
              response-body (json/read-value (:body response) (json/object-mapper {:decode-key-fn true}))]
          (is (= 200 (:status response)))
          (is (= "Photo uploaded successfully" (:message response-body)))
          (is (str/starts-with? (:file-uri response-body) "media/photos/")))))))
