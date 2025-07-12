(ns rwclj.photo
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [rwclj.db :as db])
  (:import [com.drew.imaging ImageMetadataReader]
           [com.drew.metadata.exif ExifSubIFDDirectory]
           [org.apache.jena.rdf.model ModelFactory Resource]
           [org.apache.jena.vocabulary RDF VCARD]))

(defn- get-exif-date-time [metadata]
  (when-let [directory (.getDirectory metadata ExifSubIFDDirectory)]
    (when-let [date (.getDate directory ExifSubIFDDirectory/TAG_DATETIME_ORIGINAL)]
      (.toInstant date))))

(defn- create-rdf-model [metadata file-uri]
  (let [model (ModelFactory/createDefaultModel)
        photo-resource (.createResource model file-uri)]
    (.add model photo-resource RDF/type VCARD/PHOTO)
    (when-let [date-time (get-exif-date-time metadata)]
      (.add model photo-resource (.createProperty model "http://purl.org/dc/elements/1.1/date") (str date-time)))
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
        {:status 200 :body {:message "Photo uploaded successfully" :file-uri file-uri}})
      (catch Exception e
        (log/error e "Error processing photo upload")
        {:status 500 :body {:error "Error processing photo upload"}}))))
