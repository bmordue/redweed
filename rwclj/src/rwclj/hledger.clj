(ns rwclj.hledger
  (:require [clojure.string :as str]
            [ring.util.response :as response]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [rwclj.db :as db])
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]
           [java.util UUID]
           [org.apache.jena.datatypes TypeMapper]))

;; Namespace definitions
(def base-uri "http://redweed.local/")
(def fibo-be-ns "https://spec.edmcouncil.org/fibo/ontology/BE/FunctionalEntities/BusinessEntities/")
(def fibo-fbc-ns "https://spec.edmcouncil.org/fibo/ontology/FBC/FunctionalEntities/FinancialBusinessAndCommerce/")
(def fibo-fi-ns "https://spec.edmcouncil.org/fibo/ontology/FI/FinancialInstruments/FinancialInstruments/")
(def xsd-ns "http://www.w3.org/2001/XMLSchema#")

;; Helper functions
(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value)))
  ([value datatype] (ResourceFactory/createTypedLiteral value datatype)))

;; Properties
(def fibo-hasTransaction (create-property (str fibo-fbc-ns "hasTransaction")))
(def fibo-hasAmount (create-property (str fibo-fi-ns "hasAmount")))
(def fibo-hasDate (create-property (str fibo-fbc-ns "hasDate")))
(def fibo-hasDescription (create-property (str fibo-be-ns "hasDescription")))

;; Types
(def fibo-Transaction (create-resource (str fibo-fbc-ns "Transaction")))

;; hledger parsing
(defn parse-hledger-journal
  "Parse hledger journal text into a sequence of transactions"
  [journal-text]
  (let [lines (str/split-lines journal-text)]
    (->> lines
         (partition-by #(= "" (str/trim %)))
         (remove #(= [""] %))
         (map (fn [transaction-lines]
                (let [header (first transaction-lines)
                      postings (rest transaction-lines)
                      [_ date description] (re-matches #"(\d{4}/\d{1,2}/\d{1,2})\s+(.*)" header)]
                  {:date date
                   :description description
                   :postings (map (fn [posting]
                                    (let [[_ account amount] (re-matches #"\s+(.*?)\s\s+(.*)" posting)]
                                      {:account account
                                       :amount amount}))
                                  postings)}))))))

(defn hledger->rdf
  "Convert hledger data to RDF triples"
  [hledger-data model]
  (let [xsd-date (-> (TypeMapper/getInstance) (.getSafeTypeByName (str xsd-ns "date")))]
    (doseq [transaction hledger-data]
      (let [transaction-uri (str base-uri "transaction/" (UUID/randomUUID))
            transaction-resource (create-resource transaction-uri)]
        (.add model transaction-resource RDF/type fibo-Transaction)
        (.add model transaction-resource fibo-hasDate (create-literal (:date transaction) xsd-date))
        (.add model transaction-resource fibo-hasDescription (create-literal (:description transaction)))
        (doseq [posting (:postings transaction)]
          (let [posting-uri (str base-uri "posting/" (UUID/randomUUID))
                posting-resource (create-resource posting-uri)]
            (.add model transaction-resource fibo-hasTransaction posting-resource)
            (.add model posting-resource fibo-hasAmount (create-literal (:amount posting)))
            ;; Link posting to an account - needs more detailed modeling
            ))))))

(defn import-hledger-handler
  "Handle hledger import POST request"
  [request]
  (let [dataset (db/get-dataset)]
    (try
      (.begin dataset :write)
      (let [body (slurp (:body request))
            hledger-data (parse-hledger-journal body)
            model (ModelFactory/createDefaultModel)]
        (hledger->rdf hledger-data model)
        (db/store-rdf-model! dataset model)
        (.commit dataset)
        (log/info "Successfully stored hledger RDF")
        (-> (response/response
             (json/write-value-as-string
              {:status "success"
               :message "hledger imported successfully"}))
            (response/content-type "application/json")
            (response/status 201)))
      (catch Exception e
        (log/error "Error processing hledger import:" (.getMessage e))
        (when (.isInTransaction dataset)
          (.abort dataset))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Internal server error"})
             )
            (response/content-type "application/json")
            (response/status 500)))
      (finally
        (when (.isInTransaction dataset)
          (.end dataset))))))
