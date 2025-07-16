(ns rwclj.eat
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
(def as-ns "https://www.w3.org/ns/activitystreams#")

;; Helper functions
(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value)))
  ([value datatype] (ResourceFactory/createTypedLiteral value datatype)))

;; Properties
(def as-actor (create-property (str as-ns "actor")))
(def as-object (create-property (str as-ns "object")))
(def as-summary (create-property (str as-ns "summary")))

;; Types
(def as-Create (create-resource (str as-ns "Create")))

(defn eat->rdf
  "Convert an 'eat' event to RDF triples"
  [summary model]
  (let [activity-uri (str base-uri "eat/" (UUID/randomUUID))
        activity (create-resource activity-uri)
        person-uri (str base-uri "person/some-person") ;; placeholder
        person (create-resource person-uri)
        object-uri (str base-uri "article/" (UUID/randomUUID))
        object (create-resource object-uri)]

    ;; Activity
    (doto model
      (.add activity RDF/type as-Create)
      (.add activity as-actor person)
      (.add activity as-object object))

    ;; Object
    (doto model
      (.add object as-summary (create-literal summary)))

    activity-uri))

;; HTTP handlers
(defn eat-handler
  "Handle eat event POST request"
  [request]
  (try
    (let [body (slurp (:body request))
          json-data (json/read-value body)
          summary (get json-data "summary")]
      (if summary
        (let [model (ModelFactory/createDefaultModel)
              activity-uri (eat->rdf summary model)]
          (db/store-rdf-model! db/get-dataset model)
          (log/info "Successfully stored eat event RDF:" activity-uri)
          (-> (response/response
               (json/write-value-as-string
                {:status "success"
                 :activity-uri activity-uri
                 :message "Eat event recorded successfully"}))
              (response/content-type "application/json")
              (response/status 201)))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Invalid or missing summary"}))
            (response/content-type "application/json")
            (response/status 400))))
    (catch Exception e
      (log/error "Error processing eat event:" (.getMessage e))
      (-> (response/response
           (json/write-value-as-string
            {:status "error"
             :message "Internal server error"}))
          (response/content-type "application/json")
          (response/status 500)))))
