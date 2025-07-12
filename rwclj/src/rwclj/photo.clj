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

(defn create-rdf-model [metadata file-uri]
  (let [model (ModelFactory/createDefaultModel)
        photo-resource (.createResource model file-uri)]
    (.add model photo-resource RDF/type VCARD/PHOTO)
    (when-let [date-time (get-exif-date-time metadata)]
      (.add model photo-resource (.createProperty model "http://purl.org/dc/elements/1.1/date") (str date-time)))
    model))

(defn extract-exif-metadata [file]
  (with-open [stream (io/input-stream file)]
    (ImageMetadataReader/readMetadata stream)))
