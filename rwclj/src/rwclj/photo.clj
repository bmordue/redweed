(ns rwclj.photo
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [rwclj.db :as db]
            [ring.util.response :as response]
            [jsonista.core :as json])
  (:import [com.drew.imaging ImageMetadataReader]
           [com.drew.metadata.exif ExifSubIFDDirectory]
           [org.apache.jena.rdf.model ModelFactory Resource]
           [org.apache.jena.vocabulary DC RDF]))

(defn- get-exif-date-time [metadata]
  (when-let [directory (.getDirectory metadata ExifSubIFDDirectory)]
    (when-let [date (.getDate directory ExifSubIFDDirectory/TAG_DATETIME_ORIGINAL)]
      (.toInstant date))))

(defn- create-rdf-model [metadata file-uri]
  (let [model (ModelFactory/createDefaultModel)
        photo-resource (.createResource model file-uri)]
    (.add model photo-resource DC/type "StillImage")
    (when-let [date-time (get-exif-date-time metadata)]
      (.add model photo-resource DC/date (str date-time)))
    model))

(defn extract-exif-metadata [file]
  (with-open [stream (io/input-stream file)]
    (ImageMetadataReader/readMetadata stream)))

(defn process-photo-upload [request]
  (let [temp-file (-> request :params :file :tempfile)
        original-filename (-> request :params :file :filename)
        file-uri (str "media/photos/" original-filename)]
    (try
      (io/copy temp-file (io/file file-uri))
      (let [metadata (extract-exif-metadata (io/file file-uri))
            rdf-model (create-rdf-model metadata file-uri)]
        (db/store-rdf-model! (db/get-dataset) rdf-model)
        (-> (response/response (json/write-value-as-string {:message "Photo uploaded successfully" :file-uri file-uri}))
            (response/status 200)
            (response/header "Content-Type" "application/json")))
      (catch Exception e
        (log/error e "Error processing photo upload")
        (-> (response/response (json/write-value-as-string {:error "Error processing photo upload"}))
            (response/status 500)
            (response/header "Content-Type" "application/json"))))))
