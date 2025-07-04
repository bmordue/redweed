(ns redeemed.server
  (:require [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [response status]]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [redweed.api.vcard :as vcard] ; Moved from duplicate
            [my-clojure-project.db :as db] ; Moved from duplicate and added db require
            [ring.swagger.ui :as swagger-ui]
            [ring.swagger.core :as swagger])
  (:import [org.apache.jena.rdf.model ModelFactory] ; Kept ModelFactory for vcard import if needed directly
           [java.time LocalDate]
           [java.time.format DateTimeFormatter])
  (:gen-class))

;; SPARQL Queries
(def list-contacts-query "
  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

  SELECT ?person ?name ?givenName ?familyName WHERE {
    ?person a foaf:Person .
    ?person foaf:name ?name .
    OPTIONAL { ?person foaf:givenName ?givenName }
    OPTIONAL { ?person foaf:familyName ?familyName }
  }
  ORDER BY ?name")

(defn get-contact-by-name-query [escaped-name]
  (str "
  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
  PREFIX event: <http://purl.org/NET/c4dm/event.owl#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

  SELECT ?person ?name ?givenName ?familyName ?event ?eventLabel ?eventTime WHERE {
    ?person a foaf:Person .
    ?person foaf:name \"" escaped-name "\" .
    OPTIONAL { ?person foaf:givenName ?givenName }
    OPTIONAL { ?person foaf:familyName ?familyName }
    OPTIONAL {
      ?event event:agent ?person .
      ?event rdfs:label ?eventLabel .
      OPTIONAL { ?event event:time ?eventTime }
    }
  }"))

(defn list-events-in-range-query [start-date end-date]
  (str "
  PREFIX event: <http://purl.org/NET/c4dm/event.owl#>
  PREFIX schema: <http://schema.org/>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX foaf: <http://xmlns.com/foaf/0.1/>

  SELECT ?event ?label ?time ?startDate ?agent ?agentName ?place ?placeLabel WHERE {
    ?event a event:Event .
    OPTIONAL { ?event rdfs:label ?label }
    OPTIONAL { ?event event:time ?time }
    OPTIONAL { ?event schema:startDate ?startDate }
    OPTIONAL {
      ?event event:agent ?agent .
      ?agent foaf:name ?agentName
    }
    OPTIONAL {
      ?event event:place ?place .
      ?place rdfs:label ?placeLabel
    }
    
    FILTER (
      (BOUND(?time) && ?time >= \"" start-date "T00:00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime> &&
       ?time <= \"" end-date "T23:59:59\"^^<http://www.w3.org/2001/XMLSchema#dateTime>) ||
      (BOUND(?startDate) && ?startDate >= \"" start-date "T00:00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime> &&
       ?startDate <= \"" end-date "T23:59:59\"^^<http://www.w3.org/2001/XMLSchema#dateTime>)
    )
  }
  ORDER BY ?time ?startDate"))

(def list-places-query "
  PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>
  PREFIX schema: <http://schema.org/>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

  SELECT ?place ?label ?lat ?long ?type WHERE {
    ?place a geo:SpatialThing .
    OPTIONAL { ?place rdfs:label ?label }
    OPTIONAL { ?place geo:lat ?lat }
    OPTIONAL { ?place geo:long ?long }
    OPTIONAL {
      ?place a ?type .
      FILTER(?type != <http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing>)
    }
  }
  ORDER BY ?label")

;; API endpoint handlers
(defn list-contacts []
  (response {:contacts (db/execute-sparql-select list-contacts-query)}))

(defn get-contact-by-name [full-name]
  (let [escaped-name (str/replace full-name "\"" "\\\"")
        query (get-contact-by-name-query escaped-name)
        results (db/execute-sparql-select query)]
    (if (empty? results)
      (status (response {:error "Contact not found"}) 404)
      (response {:contact (first results)
                 :events (map #(select-keys % [:event :eventLabel :eventTime]) results)}))))

(defn list-events-in-range [start-date end-date]
  (let [query (list-events-in-range-query start-date end-date)]
    (response {:events (db/execute-sparql-select query)
               :date-range {:start start-date :end end-date}})))

(defn list-places []
  (response {:places (db/execute-sparql-select list-places-query)}))

;; Routes
(defroutes app-routes
  ;; Health check
  (GET "/health" []
    {:summary "Health check endpoint"
     :responses {200 {:body {:status string? :service string?}}}
     :handler (fn [_] (response/response {:status "ok" :service "redeemed"}))}) ; Updated service name

  ;; vCard import endpoint
  (POST "/api/vcard/import" request
    {:summary "Import vCard data to RDF store"
     :consumes ["text/vcard" "application/json"]
     :parameters {:body {:vcard string?}}
     :responses {200 {:body {:message string?}}
                 400 {:body {:error string?}}}
     :handler vcard/import-vcard-handler})

  ;; API documentation
  (swagger-ui/create-swagger-ui-handler {:path "/api-docs"})
  (GET "/swagger.json" []
    (response (swagger/swagger-json #'app-routes)))

  ;; Existing redeemed.server routes (assuming they will be added here)
  (GET "/contacts" []
    {:summary "List all contacts"
     :responses {200 {:body {:contacts list?}}}
     :handler (fn [_] (list-contacts))})
  (GET "/contacts/:name" [name]
    {:summary "Get contact by name"
     :parameters {:path {:name string?}}
     :responses {200 {:body {:contact map? :events list?}}
                 404 {:body {:error string?}}}
     :handler (fn [_] (get-contact-by-name name))})
  (GET "/events" [start_date end_date]
    {:summary "List events in date range"
     :parameters {:query {:start_date string? :end_date string?}}
     :responses {200 {:body {:events list? :date-range map?}}}
     :handler (fn [_] (list-events-in-range start_date end_date))})
  (GET "/places" []
    {:summary "List all places"
     :responses {200 {:body {:places list?}}}
     :handler (fn [_] (list-places))})

  ;; 404 handler
  (route/not-found
   (status (response {:error "Not found"}) 404))) ; Added status to 404

(def app
  (-> app-routes
      (swagger/wrap-swagger {:info {:title "Redeemed API"
                                    :version "1.0.0"
                                    :description "API for the Redeemed application"}})
      wrap-keyword-params ; Added from redeemed.server
      wrap-params         ; Added from redeemed.server
      wrap-json-body      ; Added from redeemed.server (order matters)
      wrap-json-response)) ; Added from redweed.server

(defn start-server!
  ([] (start-server! 8080))
  ([port]
   (log/info "Starting Redeemed server on port" port) ; Updated server name
   (run-jetty app {:port port :join? false}))) ; run-jetty from redeemed.server

(defn -main [& args]
  (let [port (if (first args)
               (Integer/parseInt (first args))
               8080)]
    (start-server! port)
    (log/info "Redeemed server running on port" port))) ; Updated server name

;; For REPL development
(comment
  (def server (start-server! 8080))
  (.stop server))
