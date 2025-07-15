(ns rwclj.ical-test
  (:require [clojure.test :refer :all]
            [rwclj.ical :as ical]
            [rwclj.db :as db]
            [ring.mock.request :as mock]
            [clojure.java.io :as io])
  (:import [org.apache.jena.rdf.model ModelFactory]))

(def sample-ical
  "BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//hacksw/handcal//NONSGML v1.0//EN
BEGIN:VEVENT
UID:19970610T172345Z-AF23B2@example.com
DTSTAMP:19970610T172345Z
DTSTART:19970714T170000Z
DTEND:19970715T040000Z
SUMMARY:Bastille Day Party
END:VEVENT
END:VCALENDAR")

(deftest import-ical-test
  (testing "Import an iCalendar file"
    (let [request (-> (mock/request :post "/api/ical/import")
                      (mock/header "Content-Type" "text/calendar")
                      (mock/body sample-ical))
          response (ical/import-ical-handler request)
          dataset (db/get-dataset)
          model (.getDefaultModel dataset)]
      (is (= 201 (:status response)))
      (is (.contains model
                     (ical/create-resource "http://redweed.local/event/19970610T172345Z-AF23B2@example.com")
                     ical/rdfs-label
                     (ical/create-literal "Bastille Day Party"))))))

(defn -main [& args]
  (run-tests 'rwclj.ical-test))
