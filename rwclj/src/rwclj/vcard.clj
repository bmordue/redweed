(ns rwclj.vcard
  (:require [clojure.string :as str]
            [ring.util.response :as response]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [rwclj.db :as db])
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]
           [java.util UUID]))

;; Namespace definitions
(def base-uri "http://redweed.local/")
(def foaf-ns "http://xmlns.com/foaf/0.1/")
(def vcard-ns "http://www.w3.org/2006/vcard/ns#")

;; Helper functions
(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value)))
  ([value datatype] (ResourceFactory/createTypedLiteral value datatype)))

;; Properties
(def foaf-name (create-property (str foaf-ns "name")))
(def foaf-givenName (create-property (str foaf-ns "givenName")))
(def foaf-familyName (create-property (str foaf-ns "familyName")))
(def foaf-mbox (create-property (str foaf-ns "mbox")))
(def foaf-phone (create-property (str foaf-ns "phone")))
(def vcard-hasEmail (create-property (str vcard-ns "hasEmail")))
(def vcard-hasTelephone (create-property (str vcard-ns "hasTelephone")))
(def vcard-hasAddress (create-property (str vcard-ns "hasAddress")))
(def vcard-organization-name (create-property (str vcard-ns "organization-name")))

;; Types
(def foaf-Person (create-resource (str foaf-ns "Person")))
(def vcard-Individual (create-resource (str vcard-ns "Individual")))

;; vCard parsing
(defn parse-vcard-line [line]
  (when-let [match (re-matches #"([^:;]+)(?:;[^:]*)?:(.+)" (str/trim line))]
    [(str/upper-case (second match)) (str/trim (nth match 2))]))

(defn parse-vcard
  "Parse vCard text into a map of properties"
  [vcard-text]
  (let [lines (str/split-lines vcard-text)
        ;; Filter out BEGIN and END lines before parsing
        data-lines (filter #(and (not (str/starts-with? % "BEGIN:"))
                                 (not (str/starts-with? % "END:"))) lines)
        properties (keep parse-vcard-line data-lines)]
    (reduce (fn [acc [prop value]]
              (update acc prop (fnil conj []) value))
            {} properties)))

(defn generate-person-uri
  "Generate a URI for a person based on vCard data"
  [vcard-data]
  (let [fn-name (first (get vcard-data "FN" []))
        slug (when fn-name
               (-> fn-name
                   str/lower-case
                   (str/replace #"[^a-z0-9\s]" "")
                   (str/replace #"\s+" "-")))
        uuid (str (UUID/randomUUID))]
    (str base-uri "person/" (or slug uuid))))

(defn vcard->rdf
  "Convert vCard data to RDF triples"
  [vcard-data person-uri model]
  (let [person (create-resource person-uri)]

    ;; Basic person type
    (doto model
      (.add person RDF/type foaf-Person)
      (.add person RDF/type vcard-Individual))

    ;; Full name
    (when-let [fn-name (first (get vcard-data "FN"))]
      (.add model person foaf-name (create-literal fn-name)))

    ;; Structured name
    (when-let [n-value (first (get vcard-data "N"))]
      (let [name-parts (str/split n-value #";")
            family-name (first name-parts)
            given-name (second name-parts)]
        (when (and family-name (not (str/blank? family-name)))
          (.add model person foaf-familyName (create-literal family-name)))
        (when (and given-name (not (str/blank? given-name)))
          (.add model person foaf-givenName (create-literal given-name)))))

    ;; Email addresses
    (doseq [email (get vcard-data "EMAIL" [])]
      (.add model person foaf-mbox (create-resource (str "mailto:" email))))

    ;; Phone numbers
    (doseq [tel (get vcard-data "TEL" [])]
      (.add model person foaf-phone (create-literal tel)))

    ;; Organization
    (when-let [org (first (get vcard-data "ORG"))]
      (.add model person vcard-organization-name (create-literal org)))

    ;; Address (simplified - vCard addresses are complex)
    (when-let [adr (first (get vcard-data "ADR"))]
      (.add model person vcard-hasAddress (create-literal adr)))

    person-uri))

(defn validate-vcard
  "Basic vCard validation"
  [vcard-text]
  (and (str/includes? vcard-text "BEGIN:VCARD")
       (str/includes? vcard-text "END:VCARD")
       (str/includes? vcard-text "VERSION:")))


(defn import-vcard-handler
  "Handler for importing vCard data via HTTP requests"
  [dataset request]
  (try
    (let [content-type (get-in request [:headers "content-type"])
          body (:body request)]

      (cond
        ;; Handle text/vcard content type
        (= content-type "text/vcard")
        (let [vcard-text (slurp body)]
          (if (validate-vcard vcard-text)
            (let [vcard-data (parse-vcard vcard-text)
                  person-uri (generate-person-uri vcard-data)
                  model (ModelFactory/createDefaultModel)]
              (vcard->rdf vcard-data person-uri model)
              (db/store-rdf-model! dataset model)
              (log/info "Successfully imported vCard for person:" person-uri)
              (-> (response/response (json/write-value-as-string
                                      {:status "success"
                                       :person-uri person-uri
                                       :message "vCard imported successfully"}))
                  (response/status 201)
                  (response/header "Content-Type" "application/json")))
            (-> (response/response (json/write-value-as-string
                                    {:status "error"
                                     :message "Invalid vCard format"}))
                (response/status 400)
                (response/header "Content-Type" "application/json"))))

        ;; Handle application/json content type
        (= content-type "application/json")
        (let [json-data (json/read-value (slurp body))
              vcard-text (get json-data "vcard")]
          (if (and vcard-text (validate-vcard vcard-text))
            (let [vcard-data (parse-vcard vcard-text)
                  person-uri (generate-person-uri vcard-data)
                  model (ModelFactory/createDefaultModel)]
              (vcard->rdf vcard-data person-uri model)
              (db/store-rdf-model! dataset model)
              (log/info "Successfully imported vCard from JSON for person:" person-uri)
              (-> (response/response (json/write-value-as-string
                                      {:status "success"
                                       :person-uri person-uri
                                       :message "vCard imported successfully"}))
                  (response/status 201)
                  (response/header "Content-Type" "application/json")))
            (-> (response/response (json/write-value-as-string
                                    {:status "error"
                                     :message "Invalid vCard format"}))
                (response/status 400)
                (response/header "Content-Type" "application/json"))))

        ;; Unsupported content type
        :else
        (-> (response/response (json/write-value-as-string
                                {:status "error"
                                 :message "Unsupported content type"}))
            (response/status 415)
            (response/header "Content-Type" "application/json"))))

    (catch Exception e
      (log/error "Error during vCard import:" (.getMessage e))
      (-> (response/response (json/write-value-as-string
                              {:status "error"
                               :message "Internal server error during import"}))
          (response/status 500)
          (response/header "Content-Type" "application/json")))))


;; Test data and utilities
;; TODO: move these to test/rwclj/vcard_test.clj
(def sample-vcard
  "BEGIN:VCARD
VERSION:3.0
FN:John Doe
N:Doe;John;;;
EMAIL:john.doe@example.com
TEL:+1-555-123-4567
ORG:Example Corp
ADR:;;123 Main St;Anytown;State;12345;Country
END:VCARD")

(defn test-vcard-import
  "Test function for vCard import"
  []
  (let [vcard-data (parse-vcard sample-vcard)
        person-uri (generate-person-uri vcard-data)
        model (ModelFactory/createDefaultModel)]

    (vcard->rdf vcard-data person-uri model)
    (db/store-rdf-model! db/get-dataset model)
    (log/info "Test vCard imported for person:" person-uri)

    (println "Test vCard imported:")
    (println "Person URI:" person-uri)
    (println "vCard data:" vcard-data)))

