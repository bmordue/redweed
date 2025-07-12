(ns rwclj.seed
  (:import [org.apache.jena.rdf.model ResourceFactory ModelFactory]
           [org.apache.jena.vocabulary RDF RDFS]
           [org.apache.jena.datatypes.xsd XSDDatatype]
           [org.apache.jena.tdb2 TDB2Factory]
           [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

;; Namespace definitions
(def base-uri "http://redweed.local/")
(def foaf-ns "http://xmlns.com/foaf/0.1/")
(def event-ns "http://purl.org/NET/c4dm/event.owl#")
(def geo-ns "http://www.w3.org/2003/01/geo/wgs84_pos#")
(def schema-ns "http://schema.org/")
(def time-ns "http://www.w3.org/2006/time#")

;; Helper functions
(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal 
  ([value] (ResourceFactory/createPlainLiteral (str value)))
  ([value datatype] (ResourceFactory/createTypedLiteral value datatype)))

(defn datetime-literal [datetime-str]
  (create-literal datetime-str XSDDatatype/XSDdateTime))

;; URIs
(def ben-uri (str base-uri "person/ben-mordue"))
(def ruth-uri (str base-uri "person/ruth-mordue"))
(def bennachie-uri (str base-uri "place/bennachie"))
(def car-park-uri (str base-uri "place/bennachie-car-park"))
(def hike-event-uri (str base-uri "event/bennachie-hike-2025-07-01"))
(def departure-uri (str base-uri "event/departure-2025-07-01"))
(def summit-uri (str base-uri "event/summit-2025-07-01"))
(def return-uri (str base-uri "event/return-2025-07-01"))

;; Properties
(def foaf-name (create-property (str foaf-ns "name")))
(def foaf-givenName (create-property (str foaf-ns "givenName")))
(def foaf-familyName (create-property (str foaf-ns "familyName")))
(def event-agent (create-property (str event-ns "agent")))
(def event-place (create-property (str event-ns "place")))
(def event-time (create-property (str event-ns "time")))
(def event-sub_event (create-property (str event-ns "sub_event")))
(def geo-lat (create-property (str geo-ns "lat")))
(def geo-long (create-property (str geo-ns "long")))
(def schema-startDate (create-property (str schema-ns "startDate")))
(def schema-location (create-property (str schema-ns "location")))
(def time-hasBeginning (create-property (str time-ns "hasBeginning")))
(def time-hasEnd (create-property (str time-ns "hasEnd")))
(def time-inXSDDateTime (create-property (str time-ns "inXSDDateTime")))

;; Types
(def foaf-Person (create-resource (str foaf-ns "Person")))
(def event-Event (create-resource (str event-ns "Event")))
(def geo-SpatialThing (create-resource (str geo-ns "SpatialThing")))
(def schema-Mountain (create-resource (str schema-ns "Mountain")))
(def schema-ExerciseAction (create-resource (str schema-ns "ExerciseAction")))

(defn seed-data! [& [dataset-path]]
  (println "Seeding Redweed database with Bennachie hike data..."))

;; Query function to verify data
(defn query-hike-data []
  (let [dataset-path (or (System/getenv "JENA_DB_PATH") "data/tdb2")
        dataset (TDB2Factory/connectDataset dataset-path)
        model (.getDefaultModel dataset)]
    
    (println "\n=== Bennachie Hike Data ===")
    (println "People:")
    (println "- Ben Mordue")
    (println "- Ruth Mordue")
    (println "\nPlaces:")
    (println "- Bennachie (57.2892, -2.5164)")
    (println "- Bennachie Car Park (57.2850, -2.5200)")
    (println "\nEvents:")
    (println "- 08:00 - Departure from Car Park")
    (println "- 10:00 - Reaching Summit")
    (println "- 11:30 - Return to Car Park")
    (println (str "\nTotal triples in database: " (.size model)))
    
    (.close dataset)))

;; Main execution
(defn -main [& args]
  (seed-data!)
  (query-hike-data))

;; For REPL usage
(comment
  (seed-data!)
  (query-hike-data!))
