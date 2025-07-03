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
            [my-clojure-project.db :as db]) ; Moved from duplicate and added db require
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
    (response/response {:status "ok" :service "redeemed"})) ; Updated service name

  ;; vCard import endpoint
  (POST "/api/vcard/import" request
    (vcard/import-vcard-handler request))

  ;; API documentation
  (GET "/api" []
    (response/response
     {:endpoints
      [{:path "/contacts" :method "GET" :description "List all contacts"}
       {:path "/contacts/:name" :method "GET" :description "Get contact by name"}
       {:path "/events" :method "GET" :description "List events in date range (requires start_date and end_date params)"}
       {:path "/places" :method "GET" :description "List all places"}
       {:method "POST"
        :path "/api/vcard/import"
        :description "Import vCard data to RDF store"
        :content-types ["text/vcard" "application/json"]
        :example-json {:vcard "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\n..."}}
       {:method "GET"
        :path "/health"
        :description "Health check endpoint"}]}))

  ;; Existing redeemed.server routes (assuming they will be added here)
  (GET "/contacts" [] (list-contacts))
  (GET "/contacts/:name" [name] (get-contact-by-name name))
  (GET "/events" [start_date end_date] (list-events-in-range start_date end_date))
  (GET "/places" [] (list-places))

  ;; 404 handler
  (route/not-found
   (status (response {:error "Not found"}) 404))) ; Added status to 404

(def app
  (-> app-routes
      wrap-keyword-params ; Added from redeemed.server
      wrap-params         ; Added from redeemed.server
      wrap-json-body      ; Added from redeemed.server (order matters)
      wrap-json-response
      wrap-json-params)) ; Added from redweed.server

(defn start-server!
  ([] (start-server! 8080))
  ([port]
   (log/info "Starting Redeemed server on port" port) ; Updated server name
   (run-jetty app {:port port :join? false}))) ; run-jetty from redeemed.server

(defn parse-port [args]
  (let [port-str (first args)]
    (if port-str
      (try
        (Integer/parseInt port-str)
        (catch NumberFormatException _
          (log/warn "Invalid port specified:" port-str ". Falling back to default port 8080.")
          8080))
      8080)))

(defn -main [& args]
  (let [port (parse-port args)]
    (start-server! port)
    (log/info "Redeemed server running on port" port))) ; Updated server name

;; For REPL development
(comment
  (def server (start-server! 8080))
  (.stop server))
