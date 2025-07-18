(ns rwclj.epub
  (:require [clojure.tools.logging :as log]
            [rwclj.db :as db]
            [jsonista.core :as json]
            [ring.util.response :as response]
            [rwclj.photo :as photo])
  (:import [com.adobe.epubcheck.api EpubCheck]
           [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]))

(def base-uri "http://redweed.local/")
(def bibo-ns "http://purl.org/ontology/bibo/")
(def dc-ns "http://purl.org/dc/elements/1.1/")

(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value))))

(def bibo-book (create-resource (str bibo-ns "Book")))
(def dc-title (create-property (str dc-ns "title")))
(def dc-creator (create-property (str dc-ns "creator")))
(def dc-publisher (create-property (str dc-ns "publisher")))
(def dc-date (create-property (str dc-ns "date")))

(defn extract-epub-metadata [file]
  (try
    (let [epub-check (EpubCheck. file)
          opf (.. epub-check getPackage documenti getOpfResource)]
      (with-open [in (.getInputStream opf)]
        (let [doc (javax.xml.parsers.DocumentBuilderFactory/newInstance)
              builder (.newDocumentBuilder doc)
              xml-doc (.parse builder in)
              metadata-element (-> xml-doc
                                   (.getElementsByTagName "metadata")
                                   (.item 0))]
          {:title (-> metadata-element
                      (.getElementsByTagName "dc:title")
                      (.item 0)
                      (.getTextContent))
           :creator (-> metadata-element
                        (.getElementsByTagName "dc:creator")
                        (.item 0)
                        (.getTextContent))
           :publisher (-> metadata-element
                          (.getElementsByTagName "dc:publisher")
                          (.item 0)
                          (.getTextContent))
           :date (-> metadata-element
                     (.getElementsByTagName "dc:date")
                     (.item 0)
                     (.getTextContent))})))
    (catch Exception e
      (log/error "Failed to extract EPUB metadata:" (.getMessage e))
      nil)))

(defn epub->rdf [metadata model]
  (let [book-uri (str base-uri "book/" (java.util.UUID/randomUUID))
        book (create-resource book-uri)]
    (.add model book RDF/type bibo-book)
    (when-let [title (:title metadata)]
      (.add model book dc-title (create-literal title)))
    (when-let [creator (:creator metadata)]
      (.add model book dc-creator (create-literal creator)))
    (when-let [publisher (:publisher metadata)]
      (.add model book dc-publisher (create-literal publisher)))
    (when-let [date (:date metadata)]
      (.add model book dc-date (create-literal date)))
    book-uri))

(defn import-epub-handler [request]
  (let [dataset (db/get-dataset)]
    (try
      (let [temp-file (get-in request [:multipart-params "file" :tempfile])
            metadata (extract-epub-metadata temp-file)
            model (ModelFactory/createDefaultModel)
            book-uri (epub->rdf metadata model)
            file-uri (photo/save-media-file temp-file "epub")]
        (.begin dataset)
        (db/store-rdf-model! dataset model)
        (.commit dataset)
        (log/info "Successfully stored EPUB RDF for book:" book-uri)
        (-> (response/response
             (json/write-value-as-string
              {:status "success"
               :book-uri book-uri
               :file-uri file-uri
               :message "EPUB imported successfully"}))
            (response/content-type "application/json")
            (response/status 201)))
      (catch Exception e
        (.abort dataset)
        (log/error "Error processing EPUB import:" (.getMessage e))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Internal server error"}))
            (response/content-type "application/json")
            (response/status 500))))))
