None.apache.jena.vocabulary RDF RDFS]
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

(defn seed-data! []
  (let [dataset-path (or (System/getenv "JENA_DB_PATH") "data/tdb2")
        dataset (TDB2Factory/connectDataset dataset-path)
        model (.getDefaultModel dataset)]
    
    (println "Seeding Redweed database with Bennachie hike data...")
    
    ;; Begin transaction
    (.begin dataset)
    
    try
      ;; People
      (doto model
        (.add (create-resource ben-uri) RDF/type foaf-Person)
        (.add (create-resource ben-uri) foaf-name (create-literal "Ben Mordue"))
        (.add (create-resource ben-uri) foaf-givenName (create-literal "Ben"))
        (.add (create-resource ben-uri) foaf-familyName (create-literal "Mordue"))
        
        (.add (create-resource ruth-uri) RDF/type foaf-Person)
        (.add (create-resource ruth-uri) foaf-name (create-literal "Ruth Mordue"))
        (.add (create-resource ruth-uri) foaf-givenName (create-literal "Ruth"))
        (.add (create-resource ruth-uri) foaf-familyName (create-literal "Mordue")))
      
      ;; Places
      (doto model
        ;; Bennachie summit
        (.add (create-resource bennachie-uri) RDF/type geo-SpatialThing)
        (.add (create-resource bennachie-uri) RDF/type schema-Mountain)
        (.add (create-resource bennachie-uri) RDFS/label (create-literal "Bennachie"))
        (.add (create-resource bennachie-uri) geo-lat (create-literal "57.2892" XSDDatatype/XSDdecimal))
        (.add (create-resource bennachie-uri) geo-long (create-literal "-2.5164" XSDDatatype/XSDdecimal))
        
        ;; Car park
        (.add (create-resource car-park-uri) RDF/type geo-SpatialThing)
        (.add (create-resource car-park-uri) RDFS/label (create-literal "Bennachie Car Park"))
        (.add (create-resource car-park-uri) geo-lat (create-literal "57.2850" XSDDatatype/XSDdecimal))
        (.add (create-resource car-park-uri) geo-long (create-literal "-2.5200" XSDDatatype/XSDdecimal)))
      
      ;; Main hike event
      (doto model
        (.add (create-resource hike-event-uri) RDF/type event-Event)
        (.add (create-resource hike-event-uri) RDF/type schema-ExerciseAction)
        (.add (create-resource hike-event-uri) RDFS/label (create-literal "Bennachie Hike - July 1, 2025"))
        (.add (create-resource hike-event-uri) event-agent (create-resource ben-uri))
        (.add (create-resource hike-event-uri) event-agent (create-resource ruth-uri))
        (.add (create-resource hike-event-uri) schema-startDate (datetime-literal "2025-07-01T08:00:00"))
        (.add (create-resource hike-event-uri) schema-location (create-resource bennachie-uri)))
      
      ;; Sub-events
      (doto model
        ;; Departure from car park
        (.add (create-resource departure-uri) RDF/type event-Event)
        (.add (create-resource departure-uri) RDFS/label (create-literal "Departure from Car Park"))
        (.add (create-resource departure-uri) event-time (datetime-literal "2025-07-01T08:00:00"))
        (.add (create-resource departure-uri) event-place (create-resource car-park-uri))
        (.add (create-resource departure-uri) event-agent (create-resource ben-uri))
        (.add (create-resource departure-uri) event-agent (create-resource ruth-uri))
        
        ;; Reaching summit
        (.add (create-resource summit-uri) RDF/type event-Event)
        (.add (create-resource summit-uri) RDFS/label (create-literal "Reaching Summit"))
        (.add (create-resource summit-uri) event-time (datetime-literal "2025-07-01T10:00:00"))
        (.add (create-resource summit-uri) event-place (create-resource bennachie-uri))
        (.add (create-resource summit-uri) event-agent (create-resource ben-uri))
        (.add (create-resource summit-uri) event-agent (create-resource ruth-uri))
        
        ;; Return to car park
        (.add (create-resource return-uri) RDF/type event-Event)
        (.add (create-resource return-uri) RDFS/label (create-literal "Return to Car Park"))
        (.add (create-resource return-uri) event-time (datetime-literal "2025-07-01T11:30:00"))
        (.add (create-resource return-uri) event-place (create-resource car-park-uri))
        (.add (create-resource return-uri) event-agent (create-resource ben-uri))
        (.add (create-resource return-uri) event-agent (create-resource ruth-uri)))
      
      ;; Link sub-events to main event
      (doto model
        (.add (create-resource hike-event-uri) event-sub_event (create-resource departure-uri))
        (.add (create-resource hike-event-uri) event-sub_event (create-resource summit-uri))
        (.add (create-resource hike-event-uri) event-sub_event (create-resource return-uri)))
      
      ;; Commit transaction
      (.commit dataset)
      (println "Successfully seeded database with Bennachie hike data")
      (println (str "Total triples: " (.size model)))
      
      (catch Exception e
        (.abort dataset)
        (println "Error seeding data:" (.getMessage e))
        (throw e))
      
      (finally
        (.close dataset)))))

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
