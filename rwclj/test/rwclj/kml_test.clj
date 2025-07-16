(ns rwclj.kml-test
  (:require [clojure.test :refer :all]
            [rwclj.kml :refer :all]))
  (:import [org.apache.jena.rdf.model ModelFactory]))

(def sample-kml
  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<kml xmlns=\"http://www.opengis.net/kml/2.2\">
  <Document>
    <name>Test KML</name>
    <Placemark>
      <name>Test Placemark 1</name>
      <description>This is a test placemark.</description>
      <Point>
        <coordinates>-122.0822035425683,37.42228990140251,0</coordinates>
      </Point>
    </Placemark>
    <Placemark>
      <name>Test Placemark 2</name>
      <description>Another test placemark.</description>
      <Point>
        <coordinates>-122.084,37.422,0</coordinates>
      </Point>
    </Placemark>
  </Document>
</kml>")

(deftest parse-kml-test
  (testing "Parsing a sample KML string"
    (let [parsed-data (parse-kml sample-kml)]
      (is (= 2 (count parsed-data)))
      (is (= {:name "Test Placemark 1"
              :description "This is a test placemark."
              :lat "37.42228990140251"
              :long "-122.0822035425683"}
             (first parsed-data)))
      (is (= {:name "Test Placemark 2"
              :description "Another test placemark."
              :lat "37.422"
              :long "-122.084"}
             (second parsed-data))))))

(deftest kml->rdf-test
  (testing "Converting KML data to RDF"
    (let [placemark-data {:name "Test Placemark"
                          :description "A description"
                          :lat "37.422"
                          :long "-122.084"}
          model (ModelFactory/createDefaultModel)
          place-uri (kml->rdf placemark-data model)]
      (is (not (nil? place-uri)))
      (is (.contains model
                     (create-resource place-uri)
                     rdfs-label
                     (create-literal "Test Placemark")))
      (is (.contains model
                     (create-resource place-uri)
                     schema-description
                     (create-literal "A description")))
      (is (.contains model
                     (create-resource place-uri)
                     geo-lat
                     (create-literal "37.422")))
      (is (.contains model
                     (create-resource place-uri)
                     geo-long
                     (create-literal "-122.084"))))))