(ns redweed.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.util.response :as response]
            [redweed.api.vcard :as vcard]
            [clojure.tools.logging :as log])
  (:gen-class))

(defroutes app-routes
  ;; Health check
  (GET "/health" []
    (response/response {:status "ok" :service "redweed"}))
  
  ;; vCard import endpoint
  (POST "/api/vcard/import" request
    (vcard/import-vcard-handler request))
  
  ;; API documentation
  (GET "/api" []
    (response/response 
     {:endpoints 
      [{:method "POST"
        :path "/api/vcard/import"
        :description "Import vCard data to RDF store"
        :content-types ["text/vcard" "application/json"]
        :example-json {:vcard "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\n..."}}
       {:method "GET"
        :path "/health"
        :description "Health check endpoint"}]}))
  
  ;; 404 handler
  (route/not-found 
   (response/response {:error "Not found"})))

(def app
  (-> app-routes
      wrap-json-params
      wrap-json-response))

(defn start-server! 
  ([] (start-server! 8080))
  ([port]
   (log/info "Starting Redweed server on port" port)
   (jetty/run-jetty app {:port port :join? false})))

(defn -main [& args]
  (let [port (if (first args) 
               (Integer/parseInt (first args)) 
               8080)]
    (start-server! port)
    (log/info "Redweed server running on port" port)))

;; For REPL development
(comment
  (def server (start-server! 8080))
  (.stop server))
</antArtifact>**API Usage Examples:**

**Raw vCard:**
```bash
curl -X POST http://localhost:8080/api/vcard/import \
  -H "Content-Type: text/vcard" \
  --data-binary @contact.vcf
```

**JSON with vCard:**
```bash
curl -X POST http://localhost:8080/api/vcard/import \
  -H "Content-Type: application/json" \
  -d '{"vcard": "BEGIN:VCARD\nVERSION:3.0\nFN:Jane Smith\nN:Smith;Jane;;;\nEMAIL:jane@example.com\nEND:VCARD"}'
```

**Features:**
- Parses vCard 3.0/4.0 format 
- Maps to FOAF and vCard ontologies
- Generates semantic URIs
- Stores in your TDB2 database
- Returns person URI for linking

**Start the server:**
```bash
clj -M:server
```

The API automatically converts vCard properties to RDF triples using established ontologies, making the contact data semantically queryable alongside your existing hiking data!
