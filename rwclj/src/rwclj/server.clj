(ns rwclj.server
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.response :refer [response status]]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [rwclj.vcard :as vcard]
            [rwclj.kml :as kml]
            [rwclj.db :as db])
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
  (response {:contacts (db/execute-sparql-select (db/get-dataset) list-contacts-query)}))

(defn get-contact-by-name [full-name]
  (let [escaped-name (str/replace full-name "\"" "\\\"")
        query (get-contact-by-name-query escaped-name)
        results (db/execute-sparql-select (db/get-dataset) query)]
    (if (empty? results)
      (status (response {:error "Contact not found"}) 404)
      (response {:contact (first results)
                 :events (map #(select-keys % [:event :eventLabel :eventTime]) results)}))))

(defn list-events-in-range [start-date end-date]
  (let [query (list-events-in-range-query start-date end-date)]
    (response {:events (db/execute-sparql-select (db/get-dataset) query)
               :date-range {:start start-date :end end-date}})))

(defn list-places []
  (response {:places (db/execute-sparql-select (db/get-dataset) list-places-query)}))

;; Routes
(defroutes app-routes
  ;; Health check
  (GET "/health" []
    {:summary "Health check endpoint"
     :responses {200 {:body {:status string? :service string?}}}
     :handler (fn [_] (response {:status "ok" :service "Redweed Server"}))})

  ;; vCard import endpoint
  (POST "/api/vcard/import" [request]
    {:summary "Import vCard data to RDF store"
     :consumes ["text/vcard" "application/json"]
     :parameters {:body {:vcard string?}}
     :responses {200 {:body {:message string?}}
                 400 {:body {:error string?}}}
     :handler (fn [request] (vcard/import-vcard-handler request))})

  ;; KML import endpoint
  (POST "/api/kml/import" request
    {:summary "Import KML data to RDF store"
     :consumes ["application/vnd.google-earth.kml+xml" "application/xml" "text/xml"]
     :parameters {:body string?}
     :responses {201 {:body {:message string? :place-uris list?}}
                 400 {:body {:error string?}}
                 500 {:body {:error string?}}}
     :handler (fn [request] (kml/import-kml-handler request))})

  ;; API documentation
  ;; (swagger-ui/create-swagger-ui-handler {:path "/api-docs"})
  ;; (GET "/swagger.json" []
  ;;   (response (swagger/swagger-json #'app-routes)))

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
   (status (response {:error "Not found"}) 404)))

(def app
  (-> app-routes
      ;; (swagger/wrap-swagger {:info {:title "Redweed API"
      ;;                              :version "1.0.0"
      ;;                              :description "API for the Redweed application"}})
      wrap-keyword-params
      wrap-params
      wrap-json-body
      wrap-json-response))

(defn start-server!
  ([] (start-server! 8080))
  ([port]
   (log/info "Starting Redweed server on port" port)
   (run-jetty app {:port port :join? false})))

(defn parse-port [args]
  (let [port-str (first args)]
    (try
      (let [port (Integer/parseInt port-str)]
        (if (<= 1 port 65535)
          port
          (do
            (log/warn (str "Port" port "is out of the valid range (1-65535). Falling back to default port 8080."))
            8080)))
      (catch NumberFormatException _
        (when port-str
          (log/warn (str "Invalid port specified:" port-str ". Falling back to default port 8080."))
        8080)))))

(defn -main [& args]
  (let [port (parse-port args)]
    (start-server! port)
    (log/info (str "Redweed server running on port " port))))

;; For REPL development
(comment
  (def server (start-server! 8080))
  (.stop server))
