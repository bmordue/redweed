(ns rwclj.vcard-test
  (:require [clojure.test :refer [deftest is testing]]
            [rwclj.vcard :as vcard]
            [rwclj.db :as db] ; For mocking if needed by handler tests later
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import [org.apache.jena.rdf.model Model ModelFactory Resource StmtIterator]
           [org.apache.jena.vocabulary RDF DC]
           [org.apache.jena.rdf.model ResourceFactory]))

;; Helper to get string value of a property from a model
(defn- get-property-value [^Model model ^Resource subject predicate] ; Added type hints for clarity
  (let [stmt (.getProperty model subject predicate)]
    (when stmt
      (let [object (.getObject stmt)]
        (if (.isLiteral object)
          (.getString object)
          (.toString object)))))) ; Handle non-literals too, though mostly expecting literals here

(defn- stmt-iterator->seq [^StmtIterator it]
  (iterator-seq it))

(deftest parse-vcard-line-test
  (testing "Parsing individual vCard lines"
    (is (= ["FN" "John Doe"] (vcard/parse-vcard-line "FN:John Doe")))
    (is (= ["N" "Doe;John;;;"] (vcard/parse-vcard-line "N:Doe;John;;;")))
    (is (= ["EMAIL" "john.doe@example.com"] (vcard/parse-vcard-line "EMAIL;TYPE=INTERNET:john.doe@example.com")))
    (is (= ["TEL" "+1234567890"] (vcard/parse-vcard-line "TEL;TYPE=VOICE,CELL:+1234567890")))
    (is (nil? (vcard/parse-vcard-line "MALFORMED LINE")))
    (is (= ["ORG" "Example, Inc."] (vcard/parse-vcard-line "ORG:Example, Inc.")))
    (is (= ["VERSION" "3.0"] (vcard/parse-vcard-line "VERSION:3.0")))))

(deftest parse-vcard-test
  (testing "Parsing complete vCard text"
    (let [vcard-text "BEGIN:VCARD\nVERSION:3.0\nFN:Jane Doe\nN:Doe;Jane;;;\nEMAIL:jane@example.com\nEND:VCARD"
          parsed (vcard/parse-vcard vcard-text)]
      (is (= ["Jane Doe"] (get parsed "FN")))
      (is (= ["Doe;Jane;;;"] (get parsed "N")))
      (is (= ["jane@example.com"] (get parsed "EMAIL")))
      (is (contains? parsed "VERSION"))
      (is (not (contains? parsed "BEGIN"))) ; BEGIN should not be a key
      (is (not (contains? parsed "END"))))   ; END should not be a key
    (let [vcard-multi-email "BEGIN:VCARD\nFN:Test\nEMAIL:1@test.com\nEMAIL:2@test.com\nEND:VCARD"
          parsed-multi (vcard/parse-vcard vcard-multi-email)]
      (is (= #{"1@test.com" "2@test.com"} (set (get parsed-multi "EMAIL")))))))

(deftest generate-person-uri-test
  (testing "Generating person URIs"
    (let [uri1 (vcard/generate-person-uri {"FN" ["John Doe"]})
          base-person-uri (str vcard/base-uri "person/")]
      (is (str/starts-with? uri1 base-person-uri))
      (is (str/includes? uri1 "john-doe"))
      (is (>= (count uri1) (+ (count base-person-uri) (count "john-doe")))) ; Check for UUID part
      )
    (let [uri2 (vcard/generate-person-uri {"N" ["Smith;John"]}) ; FN missing
          base-person-uri (str vcard/base-uri "person/")]
      (is (str/starts-with? uri2 base-person-uri))
      ;; No predictable slug if FN is missing, just check length for UUID
      (is (> (count uri2) (+ (count base-person-uri) 30))) ; Check for UUID part (length of UUID is 36)
      )
    (let [uri3 (vcard/generate-person-uri {}) ; Empty data
          base-person-uri (str vcard/base-uri "person/")]
      (is (str/starts-with? uri3 base-person-uri))
      (is (> (count uri3) (+ (count base-person-uri) 30))))
    (let [uri4 (vcard/generate-person-uri {"FN" [" Test User "]})] ; Name with spaces
      (is (str/includes? uri4 "test-user")))))

(deftest validate-vcard-test
  (testing "Validating vCard strings"
    (is (true? (vcard/validate-vcard "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\nEND:VCARD")))
    (is (false? (vcard/validate-vcard "BEGIN:VCARD\nFN:John Doe\nEND:VCARD"))) ; Missing VERSION
    (is (false? (vcard/validate-vcard "VERSION:3.0\nFN:John Doe\nEND:VCARD"))) ; Missing BEGIN
    (is (false? (vcard/validate-vcard "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe"))) ; Missing END
    (is (false? (vcard/validate-vcard "")))
    (is (false? (vcard/validate-vcard "Just some random text")))))

#_(deftest vcard->rdf-test
  (testing "Converting vCard data to RDF"
    (let [vcard-data {"FN" ["John Doe"]
                      "N" ["Doe;John;;;"]
                      "EMAIL" ["john.doe@example.com" "jd@work.example"]
                      "TEL" ["+1-555-123-4567"]
                      "ORG" ["Example Corp"]
                      "ADR" [";;123 Main St;Anytown;CA;90210;USA"]}
          person-uri (vcard/generate-person-uri vcard-data)
          model (ModelFactory/createDefaultModel)
          _ (vcard/vcard->rdf vcard-data person-uri model)
          person-resource (.getResource model person-uri)]

      (is (not (nil? person-resource)) "Person resource should exist in model")

      ;; Check types
      (is (.hasProperty model person-resource RDF/type (ResourceFactory/createResource (str vcard/foaf-ns "Agent"))))

      ;; Check properties
      (is (= "John Doe" (get-property-value model person-resource DC/title)))
      (let [subjects (set (map #(.getString (.getObject %)) (stmt-iterator->seq (.listStatements model person-resource DC/subject nil))))]
        (is (subjects "John"))
        (is (subjects "Doe")))

      (let [emails (set (map #(.toString (.getObject %)) (stmt-iterator->seq (.listStatements model person-resource DC/source nil))))]
        (is (= #{"mailto:john.doe@example.com" "mailto:jd@work.example"} (set (filter #(str/starts-with? % "mailto:") emails)))))

      (let [phones (set (map #(if (.isLiteral (.getObject %)) (.getString (.getObject %)) (.toString (.getObject %))) (stmt-iterator->seq (.listStatements model person-resource DC/source nil))))]
        (is (= #{"+1-555-123-4567"} (set (filter #(re-matches #"\+.*" %) phones)))))

      (is (= "Example Corp" (get-property-value model person-resource DC/publisher)))
      (is (= ";;123 Main St;Anytown;CA;90210;USA" (get-property-value model person-resource DC/source)))

      ;; Test with minimal data
      (let [minimal-vcard {"FN" ["Min Me"]}
            min-person-uri (vcard/generate-person-uri minimal-vcard)
            min-model (ModelFactory/createDefaultModel)
            _ (vcard/vcard->rdf minimal-vcard min-person-uri min-model)
            min-person-resource (.getResource min-model min-person-uri)]
        (is (= "Min Me" (get-property-value min-model min-person-resource DC/title)))
        (is (nil? (get-property-value min-model min-person-resource DC/subject)))))))

#_(deftest ^:integration import-vcard-handler-test
    (testing "vCard import handler logic (mocked DB)"
      (let [sample-vcard-text "BEGIN:VCARD\nVERSION:3.0\nFN:Test Handler\nN:Handler;Test;;;\nEMAIL:handler@example.com\nEND:VCARD"
            mock-stored-models (atom [])]
        (with-redefs [db/store-rdf-model! (fn [_ model] (swap! mock-stored-models conj model))]

          ;; Test with raw vcard
          (let [request {:headers {"content-type" "text/vcard"}
                         :body (io/input-stream (.getBytes sample-vcard-text))}
                response (vcard/import-vcard-handler request)
                body (slurp (:body response))]
            (is (= 201 (:status response)))
            (is (str/includes? body "\"status\":\"success\""))
            (is (str/includes? body "Test Handler")) ; Check if person URI (containing name) is in response
            (is (= 1 (count @mock-stored-models)))
            (let [stored-model (first @mock-stored-models)
                  person-uri-regex #"http://redweed.local/person/test-handler-[0-9a-fA-F\\-]+"]
              (is (re-find person-uri-regex body))
              (let [person-uri-match (re-find person-uri-regex body)
                    person-uri (first person-uri-match)]
                (is (= "Test Handler" (get-property-value stored-model (.getResource stored-model person-uri) DC/title))))))

          (reset! mock-stored-models []) ; Reset for next test case

          ;; Test with JSON vcard
          (let [json-body (str "{\"vcard\": \"" (str/escape sample-vcard-text {"\n" "\\n"}) "\"}")
                request {:headers {"content-type" "application/json"}
                         :body (io/input-stream (.getBytes json-body))}
                response (vcard/import-vcard-handler request)
                body (slurp (:body response))]
            (is (= 201 (:status response)))
            (is (str/includes? body "\"status\":\"success\""))
            (is (= 1 (count @mock-stored-models))))

          (reset! mock-stored-models [])

          ;; Test with invalid vcard
          (let [invalid-vcard-text "BEGIN:VCARD\nFN:Bad Card\nEND:VCARD" ; Missing VERSION
                request {:headers {"content-type" "text/vcard"}
                         :body (io/input-stream (.getBytes invalid-vcard-text))}
                response (vcard/import-vcard-handler request)
                body (slurp (:body response))]
            (is (= 400 (:status response)))
            (is (str/includes? body "Invalid vCard format"))
            (is (empty? @mock-stored-models)))

          ;; Test with unsupported content type
          (let [request {:headers {"content-type" "application/xml"}
                         :body (io/input-stream (.getBytes "<xml></xml>"))}
                response (vcard/import-vcard-handler request)
                body (slurp (:body response))]
            (is (= 415 (:status response)))
            (is (str/includes? body "Unsupported content type"))
            (is (empty? @mock-stored-models)))))))
