(ns rwclj.mp3
  (:require [clojure.tools.logging :as log]
            [rwclj.db :as db]
            [jsonista.core :as json]
            [ring.util.response :as response])
  (:import [com.mpatric.mp3agic Mp3File]
           [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]))

(def base-uri "http://redweed.local/")
(def mo-ns "http://purl.org/ontology/mo/")

(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value))))

(def mo-musical-work (create-resource (str mo-ns "MusicalWork")))
(def mo-title (create-property (str mo-ns "title")))
(def mo-artist (create-property (str mo-ns "artist")))
(def mo-album (create-property (str mo-ns "album")))
(def mo-track-number (create-property (str mo-ns "track_number")))
(def mo-genre (create-property (str mo-ns "genre")))

(defn extract-mp3-metadata [file]
  (try
    (let [mp3file (Mp3File. file)
          id3v2 (if (.hasId3v2Tag mp3file) (.getId3v2Tag mp3file))]
      (when id3v2
        {:title (.getTitle id3v2)
         :artist (.getArtist id3v2)
         :album (.getAlbum id3v2)
         :track-number (.getTrack id3v2)
         :genre (.getGenreDescription id3v2)}))
    (catch Exception e
      (log/error "Failed to extract MP3 metadata:" (.getMessage e))
      nil)))

(defn mp3->rdf [metadata model]
  (let [work-uri (str base-uri "work/" (java.util.UUID/randomUUID))
        work (create-resource work-uri)]
    (.add model work RDF/type mo-musical-work)
    (when-let [title (:title metadata)]
      (.add model work mo-title (create-literal title)))
    (when-let [artist (:artist metadata)]
      (.add model work mo-artist (create-literal artist)))
    (when-let [album (:album metadata)]
      (.add model work mo-album (create-literal album)))
    (when-let [track-number (:track-number metadata)]
      (.add model work mo-track-number (create-literal track-number)))
    (when-let [genre (:genre metadata)]
      (.add model work mo-genre (create-literal genre)))
    work-uri))

(defn import-mp3-handler [request]
  (let [dataset (db/get-dataset)]
    (try
      (let [temp-file (get-in request [:multipart-params "file" :tempfile])
            metadata (extract-mp3-metadata temp-file)
            model (ModelFactory/createDefaultModel)
            work-uri (mp3->rdf metadata model)
            file-uri (photo/save-media-file temp-file "mp3")]
        (.begin dataset)
        (db/store-rdf-model! dataset model)
        (.commit dataset)
        (log/info "Successfully stored MP3 RDF for work:" work-uri)
        (-> (response/response
             (json/write-value-as-string
              {:status "success"
               :work-uri work-uri
               :file-uri file-uri
               :message "MP3 imported successfully"}))
            (response/content-type "application/json")
            (response/status 201)))
      (catch Exception e
        (.abort dataset)
        (log/error "Error processing MP3 import:" (.getMessage e))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Internal server error"}))
            (response/content-type "application/json")
            (response/status 500))))))
