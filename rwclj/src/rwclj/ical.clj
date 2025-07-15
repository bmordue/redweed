(ns rwclj.ical
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [rwclj.db :as db])
  (:import [net.fortuna.ical4j.data CalendarBuilder]
           [net.fortuna.ical4j.model Calendar]
           [net.fortuna.ical4j.model.component VEvent]
           [java.io StringReader]
           [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]))

;; Namespace definitions
(def base-uri "http://redweed.local/")
(def event-ns "http://purl.org/NET/c4dm/event.owl#")
(def schema-ns "http://schema.org/")
(def rdfs-ns "http://www.w3.org/2000/01/rdf-schema#")

;; Helper functions
(defn create-resource [uri]
  (ResourceFactory/createResource uri))

(defn create-property [uri]
  (ResourceFactory/createProperty uri))

(defn create-literal
  ([value] (ResourceFactory/createPlainLiteral (str value)))
  ([value datatype] (ResourceFactory/createTypedLiteral value datatype)))

;; Properties
(def event-time (create-property (str event-ns "time")))
(def schema-startDate (create-property (str schema-ns "startDate")))
(def rdfs-label (create-property (str rdfs-ns "label")))

;; Types
(def event-Event (create-resource (str event-ns "Event")))

;; ICS parsing
(defn parse-ical
  "Parse iCalendar text into a Calendar object"
  [ical-text]
  (let [reader (StringReader. ical-text)
        builder (CalendarBuilder.)]
    (.build builder reader)))

(defn ical->rdf
  "Convert iCalendar data to RDF triples"
  [^Calendar calendar model]
  (doseq [event (.getComponents calendar "VEVENT")]
    (let [uid (-> event .getUid .getValue)
          event-uri (str base-uri "event/" uid)
          event-resource (create-resource event-uri)]
      (.add model event-resource RDF/type event-Event)
      (when-let [summary (-> event .getSummary .getValue)]
        (.add model event-resource rdfs-label (create-literal summary)))
      (when-let [dtstart (-> event .getStartDate .getDate)]
        (.add model event-resource schema-startDate (create-literal (str dtstart)))))))

(defn import-ical-handler
  "Handle iCalendar import POST request"
  [request]
  (try
    (let [body (slurp (:body request))
          content-type (get-in request [:headers "content-type"] "")]
      (cond
        (str/includes? content-type "text/calendar")
        (let [ical-text body
              calendar (parse-ical ical-text)
              model (ModelFactory/createDefaultModel)]
          (ical->rdf calendar model)
          (db/store-rdf-model! db/get-dataset model)
          (log/info "Successfully stored iCalendar RDF")
          {:status 201
           :headers {"Content-Type" "application/json"}
           :body "{\"status\": \"success\", \"message\": \"iCalendar imported successfully\"}"})

        (str/includes? content-type "application/json")
        (let [json-data (json/read-value body)
              ical-text (get json-data "ical")
              calendar (parse-ical ical-text)
              model (ModelFactory/createDefaultModel)]
          (ical->rdf calendar model)
          (db/store-rdf-model! db/get-dataset model)
          (log/info "Successfully stored iCalendar RDF")
          {:status 201
           :headers {"Content-Type" "application/json"}
           :body "{\"status\": \"success\", \"message\": \"iCalendar imported successfully\"}"})

        :else
        {:status 415
         :headers {"Content-Type" "application/json"}
         :body "{\"status\": \"error\", \"message\": \"Unsupported content type. Use text/calendar or application/json\"}"}))
    (catch Exception e
      (log/error "Error processing iCalendar import:" (.getMessage e))
      {:status 500
       :headers {"Content-Type" "application/json"}
       :body "{\"status\": \"error\", \"message\": \"Internal server error\"}"})))
