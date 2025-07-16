(ns rwclj.review
  (:require [clojure.tools.logging :as log]
            [rwclj.db :as db]
            [clojure.string :as str])
  (:import [org.apache.jena.update UpdateFactory UpdateExecutionFactory]
           [org.apache.jena.query QueryFactory]))

(defn build-review-insert-query [review-uri rating text]
  (str "
  PREFIX schema: <http://schema.org/>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

  INSERT DATA {
    <" review-uri "> a schema:Review .
    " (when rating (str "<" review-uri "> schema:ratingValue " rating " .")) "
    " (when text (str "<" review-uri "> schema:reviewBody \"" text "\" .")) "
  }"))

(defn import-review [review-data]
  (log/info "Importing review:" review-data)
  (let [review-uri (str "urn:uuid:" (java.util.UUID/randomUUID))
        query (build-review-insert-query review-uri (:rating review-data) (:text review-data))]
    (db/execute-sparql-update query)
    {:status "success"
     :review-uri review-uri
     :message "Review imported successfully"}))

(defn import-review-handler [request]
  (let [review-data (:body request)]
    (try
      (let [result (import-review review-data)]
        {:status 201
         :body result})
      (catch Exception e
        (log/error e "Error importing review")
        {:status 400
         :body {:error "Invalid review format"}}))))
