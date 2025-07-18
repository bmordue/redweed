(ns rwclj.mp4
  (:require [clojure.tools.logging :as log]
            [rwclj.db :as db]
            [jsonista.core :as json]
            [ring.util.response :as response]
            [rwclj.photo :as photo])
  (:import [com.googlecode.mp4parser.authoring.container.mp4 MovieCreator]
           [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]))

(def base-uri "http://redweed.local/")
(def ma-ns "http://www.w3.org/ns/ma-ont/")

(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value))))

(def ma-media-resource (create-resource (str ma-ns "MediaResource")))
(def ma-title (create-property (str ma-ns "title")))
(def ma-creation-date (create-property (str ma-ns "creationDate")))

(defn extract-mp4-metadata [file]
  (try
    (let [movie (MovieCreator/build file)]
      {:title (.. movie getMovieMetaData getTitle)
       :creation-date (.. movie getMovieMetaData getCreationTime)})
    (catch Exception e
      (log/error "Failed to extract MP4 metadata:" (.getMessage e))
      nil)))

(defn mp4->rdf [metadata model]
  (let [resource-uri (str base-uri "resource/" (java.util.UUID/randomUUID))
        resource (create-resource resource-uri)]
    (.add model resource RDF/type ma-media-resource)
    (when-let [title (:title metadata)]
      (.add model resource ma-title (create-literal title)))
    (when-let [creation-date (:creation-date metadata)]
      (.add model resource ma-creation-date (create-literal (str creation-date))))
    resource-uri))

(defn import-mp4-handler [request]
  (let [dataset (db/get-dataset)]
    (try
      (let [temp-file (get-in request [:multipart-params "file" :tempfile])
            metadata (extract-mp4-metadata (.getAbsolutePath temp-file))
            model (ModelFactory/createDefaultModel)
            resource-uri (mp4->rdf metadata model)
            file-uri (photo/save-media-file temp-file "mp4")]
        (.begin dataset)
        (db/store-rdf-model! dataset model)
        (.commit dataset)
        (log/info "Successfully stored MP4 RDF for resource:" resource-uri)
        (-> (response/response
             (json/write-value-as-string
              {:status "success"
               :resource-uri resource-uri
               :file-uri file-uri
               :message "MP4 imported successfully"}))
            (response/content-type "application/json")
            (response/status 201)))
      (catch Exception e
        (.abort dataset)
        (log/error "Error processing MP4 import:" (.getMessage e))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Internal server error"}))
            (response/content-type "application/json")
            (response/status 500))))))
