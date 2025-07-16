(ns rwclj.music
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [rwclj.db :as db])
  (:import [com.mpatric.mp3agic Mp3File]
           [org.apache.jena.vocabulary RDF]
           [org.apache.jena.rdf.model ModelFactory]))

(defn- create-rdf-model [metadata file-uri]
  (let [model (ModelFactory/createDefaultModel)
        music-resource (.createResource model file-uri)]
    (.add model music-resource RDF/type (.createResource model "http://purl.org/ontology/mo/MusicPiece"))
    (when-let [title (.getTitle metadata)]
      (.add model music-resource (.createProperty model "http://purl.org/dc/elements/1.1/title") title))
    (when-let [artist (.getArtist metadata)]
      (.add model music-resource (.createProperty model "http://purl.org/dc/elements/1.1/creator") artist))
    (when-let [album (.getAlbum metadata)]
      (.add model music-resource (.createProperty model "http://purl.org/ontology/mo/album") album))
    model))

(defn extract-mp3-metadata [file]
  (let [mp3file (Mp3File. file)]
    (when (.hasId3v2Tag mp3file)
      (.getId3v2Tag mp3file))))

(defn process-music-upload [request]
  (let [temp-file (-> request :params :file :tempfile)
        original-filename (-> request :params :file :filename)
        file-uri (str "media/music/" original-filename)]
    (try
      (io/copy temp-file (io/file file-uri))
      (let [metadata (extract-mp3-metadata (io/file file-uri))
            rdf-model (create-rdf-model metadata file-uri)]
        (db/store-rdf-model! (db/get-dataset) rdf-model)
        {:status 200 :body {:message "Music uploaded successfully" :file-uri file-uri}})
      (catch Exception e
        (log/error e "Error processing music upload")
        {:status 500 :body {:error "Error processing music upload"}}))))
