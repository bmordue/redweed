(ns rwclj.kml
  (:require [clojure.string :as str]
            [clojure.data.xml :refer [parse-str]]
            [ring.util.response :as response]
            [jsonista.core :as json]
            [clojure.tools.logging :as log]
            [rwclj.db :as db])
  (:import [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]
           [java.util UUID]))

;; Namespace definitions
(def base-uri "http://redweed.local/")
(def geo-ns "http://www.w3.org/2003/01/geo/wgs84_pos#")
(def rdfs-ns "http://www.w3.org/2000/01/rdf-schema#")
(def schema-ns "http://schema.org/")

;; Helper functions
(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value)))
  ([value datatype] (ResourceFactory/createTypedLiteral value datatype)))

;; Properties
(def rdfs-label (create-property (str rdfs-ns "label")))
(def geo-lat (create-property (str geo-ns "lat")))
(def geo-long (create-property (str geo-ns "long")))
(def schema-description (create-property (str schema-ns "description")))

;; Types
(def geo-spatial-thing (create-resource (str geo-ns "SpatialThing")))
(def schema-place (create-resource (str schema-ns "Place")))

;; KML parsing
(defn- element-seq-local
  "Returns a lazy sequence of elements from an XML tree, filtered by tags."
  ([root]
   (tree-seq :content :content root))
  ([root tags]
   (let [tag-set (if (coll? tags) (set tags) (set [tags]))]
     (filter #(and (map? %) (tag-set (:tag %))) (element-seq-local root)))))

(defn- find-first-content [element tag]
  (first (:content (first (element-seq-local element [tag])))))

(defn- parse-placemark [placemark-element]
  (let [name (find-first-content placemark-element :name)
        description (find-first-content placemark-element :description)
        coordinates-str (some-> (first (element-seq-local placemark-element :Point)) (find-first-content :coordinates))
        [long lat] (when coordinates-str (str/split coordinates-str #","))]
    {:name name
     :description description
     :lat lat
     :long long}))

(defn parse-kml [kml-string]
  (let [parsed-xml (parse-str kml-string)]
    (->> parsed-xml
         (element-seq-local [:Placemark])
         (map parse-placemark))))

(defn generate-place-uri [placemark-data]
  (let [slug (when-let [name (:name placemark-data)]
               (-> name
                   str/lower-case
                   (str/replace #"[^a-z0-9\s]" "")
                   (str/replace #"\s+" "-")))
        uuid (str (UUID/randomUUID))]
    (str base-uri "place/" (or (not-empty slug) uuid))))

(defn kml->rdf [placemark-data model]
  (let [place-uri (generate-place-uri placemark-data)
        place (create-resource place-uri)]
    (.add model place RDF/type geo-spatial-thing)
    (.add model place RDF/type schema-place)

    (when-let [name (:name placemark-data)]
      (.add model place rdfs-label (create-literal name)))

    (when-let [description (:description placemark-data)]
      (.add model place schema-description (create-literal description)))

    (when-let [lat (:lat placemark-data)]
      (.add model place geo-lat (create-literal lat)))

    (when-let [long (:long placemark-data)]
      (.add model place geo-long (create-literal long)))
    place-uri))

(defn import-kml-handler [request]
  (let [dataset (db/get-dataset)]
    (try
      (let [body (slurp (:body request))
            placemarks (parse-kml body)
            model (ModelFactory/createDefaultModel)
            place-uris (map #(kml->rdf % model) placemarks)]

        (.begin dataset)
        (db/store-rdf-model! dataset model)
        (.commit dataset)
        (log/info "Successfully stored KML RDF for places:" place-uris)

        (-> (response/response
             (json/write-value-as-string
              {:status "success"
               :place-uris place-uris
               :message "KML imported successfully"}))
            (response/content-type "application/json")
            (response/status 201)))
      (catch org.xml.sax.SAXParseException e
        (.abort dataset)
        (log/error "Failed to parse KML:" (.getMessage e))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Invalid KML format"}))
            (response/content-type "application/json")
            (response/status 400)))
      (catch Exception e
        (.abort dataset)
        (log/error "Error processing KML import:" (.getMessage e))
        (-> (response/response
             (json/write-value-as-string
              {:status "error"
               :message "Internal server error"}))
            (response/content-type "application/json")
            (response/status 500))))))
