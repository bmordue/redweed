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
            [rwclj.db :as db]
            [rwclj.photo :as photo]
            [rwclj.import :as import])
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
(defn list-contacts [dataset]
  (response {:contacts (db/execute-sparql-select dataset list-contacts-query)}))

(defn get-contact-by-name [dataset full-name]
  (let [escaped-name full-name;(str/replace full-name \"\" \"\\\"\")
        query (get-contact-by-name-query escaped-name)
        results (db/execute-sparql-select dataset query)]
    (if (empty? results)
      (status (response {:error "Contact not found"}) 404)
      (response {:contact (first results)
                 :events (map #(select-keys % [:event :eventLabel :eventTime]) results)}))))

(defn list-events-in-range [dataset start-date end-date]
  (let [query (list-events-in-range-query start-date end-date)]
    (response {:events (db/execute-sparql-select dataset query)
               :date-range {:start start-date :end end-date}})))

(defn list-places [dataset]
  (response {:places (db/execute-sparql-select dataset list-places-query)}))

;; Routes
(defn app-routes [dataset]
  (defroutes app-routes-instance
    ;; Health check
    (GET "/health" []
      (response {:status "ok" :service "Redweed Server"}))

    ;; Generic import endpoint
    (POST "/api/import/vcard" request
      (vcard/import-vcard-handler dataset request))

    (POST "/api/import/photo" request
      (import/import-handler dataset request))

    (GET "/contacts" []
      (list-contacts dataset))
    (GET "/contacts/:name" [name]
      (get-contact-by-name dataset name))
    (GET "/events" [start_date end_date]
      (list-events-in-range dataset start_date end_date))
    (GET "/places" []
      (list-places dataset))

    ;; 404 handler
    (route/not-found
     (status (response {:error "Not found"}) 404))))

(defn make-app [dataset]
  (-> (app-routes dataset)
      wrap-keyword-params
      wrap-params
      wrap-json-body
      wrap-json-response))

(defn start-server!
  ([port dataset]
   (log/info "Starting Redweed server on port" port)
   (let [app (make-app dataset)]
     (run-jetty app {:port port :join? false})))
  ([port]
   (start-server! port (db/get-dataset))))

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
          (log/warn (str "Invalid port specified:" port-str ". Falling back to default port 8080.")))
        8080))))

(defn -main [& args]
  (let [port (parse-port args)
        dataset (db/get-dataset)]
    (start-server! port dataset)
    (log/info (str "Redweed server running on port " port))))

;; For REPL development
(comment
  (def server (start-server! 8080))
  (.stop server))
