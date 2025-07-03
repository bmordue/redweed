(ns rwclj.redweed.api.vcard-test
  (:require [clojure.test :refer :all]
            [rwclj.redweed.api.vcard :as vcard-api]
            [rwclj.db :as db] ; For mocking if needed by handler tests later
            [clojure.string :as str])
  (:import [org.apache.jena.rdf.model Model ModelFactory Resource]
           [org.apache.jena.vocabulary RDF FOAF])) ; Removed VCARD, will use vcard-api's own properties

;; Helper to get string value of a property from a model
(defn- get-property-value [^Model model ^Resource subject predicate] ; Added type hints for clarity
  (let [stmt (.getProperty model subject predicate)]
    (when stmt
      (let [object (.getObject stmt)]
        (if (.isLiteral object)
          (.getString object)
          (.toString object)))))) ; Handle non-literals too, though mostly expecting literals here

(deftest parse-vcard-line-test
  (testing "Parsing individual vCard lines"
    (is (= ["FN" "John Doe"] (vcard-api/parse-vcard-line "FN:John Doe")))
    (is (= ["N" "Doe;John;;;"] (vcard-api/parse-vcard-line "N:Doe;John;;;")))
    (is (= ["EMAIL" "john.doe@example.com"] (vcard-api/parse-vcard-line "EMAIL;TYPE=INTERNET:john.doe@example.com")))
    (is (= ["TEL" "+1234567890"] (vcard-api/parse-vcard-line "TEL;TYPE=VOICE,CELL:+1234567890")))
    (is (nil? (vcard-api/parse-vcard-line "MALFORMED LINE")))
    (is (= ["ORG" "Example, Inc."] (vcard-api/parse-vcard-line "ORG:Example, Inc.")))
    (is (= ["VERSION" "3.0"] (vcard-api/parse-vcard-line "VERSION:3.0")))))

(deftest parse-vcard-test
  (testing "Parsing complete vCard text"
    (let [vcard-text "BEGIN:VCARD\nVERSION:3.0\nFN:Jane Doe\nN:Doe;Jane;;;\nEMAIL:jane@example.com\nEND:VCARD"
          parsed (vcard-api/parse-vcard vcard-text)]
      (is (= ["Jane Doe"] (get parsed "FN")))
      (is (= ["Doe;Jane;;;"] (get parsed "N")))
      (is (= ["jane@example.com"] (get parsed "EMAIL")))
      (is (contains? parsed "VERSION"))
      (is (not (contains? parsed "BEGIN"))) ; BEGIN should not be a key
      (is (not (contains? parsed "END"))))   ; END should not be a key
    (let [vcard-multi-email "BEGIN:VCARD\nFN:Test\nEMAIL:1@test.com\nEMAIL:2@test.com\nEND:VCARD"
          parsed-multi (vcard-api/parse-vcard vcard-multi-email)]
      (is (= #{"1@test.com" "2@test.com"} (set (get parsed-multi "EMAIL")))))))

(deftest generate-person-uri-test
  (testing "Generating person URIs"
    (let [uri1 (vcard-api/generate-person-uri {"FN" ["John Doe"]})
          base-person-uri (str (vcard-api/base-uri) "person/")]
      (is (str/starts-with? uri1 base-person-uri))
      (is (str/includes? uri1 "john-doe"))
      (is (> (count uri1) (+ (count base-person-uri) (count "john-doe")))) ; Check for UUID part
      )
    (let [uri2 (vcard-api/generate-person-uri {"N" ["Smith;John"]}) ; FN missing
          base-person-uri (str (vcard-api/base-uri) "person/")]
      (is (str/starts-with? uri2 base-person-uri))
      ;; No predictable slug if FN is missing, just check length for UUID
      (is (> (count uri2) (+ (count base-person-uri) 30))) ; Check for UUID part (length of UUID is 36)
      )
    (let [uri3 (vcard-api/generate-person-uri {}) ; Empty data
          base-person-uri (str (vcard-api/base-uri) "person/")]
      (is (str/starts-with? uri3 base-person-uri))
      (is (> (count uri3) (+ (count base-person-uri) 30))))
    (let [uri4 (vcard-api/generate-person-uri {"FN" [" Test User "]})] ; Name with spaces
      (is (str/includes? uri4 "test-user")))))

(deftest validate-vcard-test
  (testing "Validating vCard strings"
    (is (true? (vcard-api/validate-vcard "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\nEND:VCARD")))
    (is (false? (vcard-api/validate-vcard "BEGIN:VCARD\nFN:John Doe\nEND:VCARD"))) ; Missing VERSION
    (is (false? (vcard-api/validate-vcard "VERSION:3.0\nFN:John Doe\nEND:VCARD"))) ; Missing BEGIN
    (is (false? (vcard-api/validate-vcard "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe"))) ; Missing END
    (is (false? (vcard-api/validate-vcard "")))
    (is (false? (vcard-api/validate-vcard "Just some random text")))))

(deftest vcard->rdf-test
  (testing "Converting vCard data to RDF"
    (let [vcard-data {"FN" ["John Doe"]
                      "N" ["Doe;John;;;"]
                      "EMAIL" ["john.doe@example.com" "jd@work.example"]
                      "TEL" ["+1-555-123-4567"]
                      "ORG" ["Example Corp"]
                      "ADR" [";;123 Main St;Anytown;CA;90210;USA"]}
          person-uri (vcard-api/generate-person-uri vcard-data)
          model (ModelFactory/createDefaultModel)
          _ (vcard-api/vcard->rdf vcard-data person-uri model)
          person-resource (.getResource model person-uri)]

      (is (not (nil? person-resource)) "Person resource should exist in model")

      ;; Check types
      (is (.hasProperty model person-resource RDF/type vcard-api/foaf-Person) "Should be a foaf:Person")
      (is (.hasProperty model person-resource RDF/type vcard-api/vcard-Individual) "Should be a vcard:Individual")

      ;; Check properties using properties defined in vcard-api
      (is (= "John Doe" (get-property-value model person-resource vcard-api/foaf-name)) "FN should map to foaf:name")
      (is (= "John" (get-property-value model person-resource vcard-api/foaf-givenName)) "N given name")
      (is (= "Doe" (get-property-value model person-resource vcard-api/foaf-familyName)) "N family name")

      (let [emails (set (map #(.toString (.getObject %)) (.listStatements model person-resource vcard-api/foaf-mbox nil)))]
        (is (= #{"mailto:john.doe@example.com" "mailto:jd@work.example"} emails) "Emails should map to foaf:mbox"))

      (let [phones (set (map #(.getString (.getObject %)) (.listStatements model person-resource vcard-api/foaf-phone nil)))]
        (is (= #{"+1-555-123-4567"} phones) "TEL should map to foaf:phone"))

      (is (= "Example Corp" (get-property-value model person-resource vcard-api/vcard-organization-name)) "ORG should map to vcard:organization-name")
      (is (= ";;123 Main St;Anytown;CA;90210;USA" (get-property-value model person-resource vcard-api/vcard-hasAddress)) "ADR should map to vcard:hasAddress (simplified)")

      ;; Test with minimal data
      (let [minimal-vcard {"FN" ["Min Me"]}
            min-person-uri (vcard-api/generate-person-uri minimal-vcard)
            min-model (ModelFactory/createDefaultModel)
            _ (vcard-api/vcard->rdf minimal-vcard min-person-uri min-model)
            min-person-resource (.getResource min-model min-person-uri)]
        (is (= "Min Me" (get-property-value min-model min-person-resource FOAF/name)))
        (is (nil? (get-property-value min-model min-person-resource FOAF/givenName)))))))


(deftest import-vcard-handler-test
  (testing "vCard import handler logic (mocked DB)"
    (let [sample-vcard-text "BEGIN:VCARD\nVERSION:3.0\nFN:Test Handler\nN:Handler;Test;;;\nEMAIL:handler@example.com\nEND:VCARD"
          mock-stored-models (atom [])]
      (with-redefs [db/store-rdf-model! (fn [model] (swap! mock-stored-models conj model))]

        ;; Test with raw vcard
        (let [request {:headers {"content-type" "text/vcard"}
                       :body (java.io.ByteArrayInputStream. (.getBytes sample-vcard-text "UTF-8"))}
              response (vcard-api/import-vcard-handler request)]
          (is (= 201 (:status response)))
          (is (str/includes? (:body response) "\"status\":\"success\""))
          (is (str/includes? (:body response) "Test Handler")) ; Check if person URI (containing name) is in response
          (is (= 1 (count @mock-stored-models)))
          (let [stored-model (first @mock-stored-models)
                person-uri-regex #"http://redweed.local/person/test-handler-[0-9a-fA-F\\-]+"]
            (is (re-find person-uri-regex (:body response)))
            (let [person-uri-match (re-find person-uri-regex (:body response))
                  person-uri (first person-uri-match)]
              (is (= "Test Handler" (get-property-value stored-model (.getResource stored-model person-uri) FOAF/name))))))

        (reset! mock-stored-models []) ; Reset for next test case

        ;; Test with JSON vcard
        (let [json-body (str "{\"vcard\": \"" (str/escape sample-vcard-text {"\n" "\\n"}) "\"}")
              request {:headers {"content-type" "application/json"}
                       :body (java.io.ByteArrayInputStream. (.getBytes json-body "UTF-8"))}
              response (vcard-api/import-vcard-handler request)]
          (is (= 201 (:status response)))
          (is (str/includes? (:body response) "\"status\":\"success\""))
          (is (= 1 (count @mock-stored-models))))

        (reset! mock-stored-models [])

        ;; Test with invalid vcard
        (let [invalid-vcard-text "BEGIN:VCARD\nFN:Bad Card\nEND:VCARD" ; Missing VERSION
              request {:headers {"content-type" "text/vcard"}
                       :body (java.io.ByteArrayInputStream. (.getBytes invalid-vcard-text "UTF-8"))}
              response (vcard-api/import-vcard-handler request)]
          (is (= 400 (:status response)))
          (is (str/includes? (:body response) "Invalid vCard format"))
          (is (empty? @mock-stored-models)))

        ;; Test with unsupported content type
        (let [request {:headers {"content-type" "application/xml"}
                       :body (java.io.ByteArrayInputStream. (.getBytes "<xml></xml>" "UTF-8"))}
              response (vcard-api/import-vcard-handler request)]
          (is (= 415 (:status response)))
          (is (str/includes? (:body response) "Unsupported content type"))
          (is (empty? @mock-stored-models)))))))

(comment
  ;; For running tests in REPL
  ;; (clojure.test/run-tests 'rwclj.redweed.api.vcard-test)

  ;; Example of inspecting a model generated by vcard->rdf
  (let [vcard-data {"FN" ["John Doe"], "EMAIL" ["john@example.com"]}
        person-uri (vcard-api/generate-person-uri vcard-data)
        model (ModelFactory/createDefaultModel)]
    (vcard-api/vcard->rdf vcard-data person-uri model)
    (.write model System/out "TURTLE"))
)
