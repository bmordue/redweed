(ns rwclj.video
  (:require [clojure.tools.logging :as log]
            [rwclj.db :as db]
            [ring.util.response :as response]
            [jsonista.core :as json])
  (:import [org.apache.jena.rdf.model ModelFactory Resource]
           [org.apache.jena.vocabulary DC RDF]))

(defn- create-rdf-model [video-url]
  (let [model (ModelFactory/createDefaultModel)
        video-resource (.createResource model video-url)]
    (.add model video-resource DC/type "MovingImage")
    model))

(defn process-video-ingest [request]
  (let [video-url (get-in request [:body :url])]
    (try
      (let [rdf-model (create-rdf-model video-url)]
        (db/store-rdf-model! (db/get-dataset) rdf-model)
        (-> (response/response (json/write-value-as-string {:message "Video ingested successfully" :video-url video-url}))
            (response/status 200)
            (response/header "Content-Type" "application/json")))
      (catch Exception e
        (log/error e "Error processing video ingest")
        (-> (response/response (json/write-value-as-string {:error "Error processing video ingest"}))
            (response/status 500)
            (response/header "Content-Type" "application/json"))))))
