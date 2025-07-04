(ns rwclj.db-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [rwclj.db :as db]
            [clojure.java.io :as io])
  (:import [org.apache.jena.tdb2 TDB2Factory]
           [org.apache.jena.query Dataset]
           [org.apache.jena.rdf.model ModelFactory ResourceFactory]
           [org.apache.jena.vocabulary RDF]))

(def test-db-dir-str "target/test-jena-db")

(defn- ensure-empty-dir! [dir-str]
  (let [dir-file (io/file dir-str)]
    (when (.exists dir-file)
      (doseq [f (reverse (file-seq dir-file))]
        (io/delete-file f)))
    (.mkdirs dir-file)))

(def ^:dynamic *db* nil)

(defn db-fixture [f]
  (ensure-empty-dir! test-db-dir-str)
  (binding [*db* (TDB2Factory/connectDataset test-db-dir-str)]
    (f)
    (.close *db*))
  (ensure-empty-dir! test-db-dir-str))

(use-fixtures :each db-fixture)

(deftest get-dataset-test
  (testing "Dataset retrieval"
    (is (instance? Dataset *db*) "Should return a Dataset object")
    (is (not (.isClosed *db*)) "Dataset should be open")))

(deftest ^:kaocha/skip store-and-retrieve-model-test
  (testing "Storing and retrieving an RDF model"
    (let [model (ModelFactory/createDefaultModel)
          subject-uri "http://example.org/subject1"
          predicate-uri "http://example.org/predicate1"
          object-uri "http://example.org/object1"
          subject (ResourceFactory/createResource subject-uri)
          predicate (ResourceFactory/createProperty predicate-uri)
          object (ResourceFactory/createResource object-uri)]
      (.add model subject predicate object)

      (db/store-rdf-model! *db* model)

      (let [retrieved-data (db/execute-sparql-select
                             *db*
                             (str "SELECT ?s ?p ?o WHERE { <" subject-uri "> <" predicate-uri "> ?o }"))
            expected-result {:s subject-uri :p predicate-uri :o object-uri}]
        (is (= 1 (count retrieved-data)) "Should retrieve one triple")
        (is (= (first retrieved-data) expected-result) "Retrieved data should match stored data")))))

(deftest ^:kaocha/skip execute-sparql-select-test
  (testing "Executing SPARQL SELECT queries"
    (let [model (ModelFactory/createDefaultModel)
          person1 (ResourceFactory/createResource "http://example.org/person1")
          person2 (ResourceFactory/createResource "http://example.org/person2")]
      (.add model person1 RDF/type (ResourceFactory/createResource "http://xmlns.com/foaf/0.1/Person"))
      (.add model person1 (ResourceFactory/createProperty "http://xmlns.com/foaf/0.1/name") "Alice")
      (.add model person2 RDF/type (ResourceFactory/createResource "http://xmlns.com/foaf/0.1/Person"))
      (.add model person2 (ResourceFactory/createProperty "http://xmlns.com/foaf/0.1/name") "Bob")

      (db/store-rdf-model! *db* model)

      (let [all-persons (db/execute-sparql-select *db* "SELECT ?person ?name WHERE { ?person a <http://xmlns.com/foaf/0.1/Person> ; <http://xmlns.com/foaf/0.1/name> ?name . }")
            alice (db/execute-sparql-select *db* "SELECT ?person ?name WHERE { ?person <http://xmlns.com/foaf/0.1/name> \"Alice\" . }")]
        (is (= 2 (count all-persons)) "Should find two persons")
        (is (= 1 (count alice)) "Should find Alice")
        (is (= (-> alice first :name) "Alice") "Alice's name should be correct")))))

(deftest ^:kaocha/skip error-handling-test
  (testing "Error handling for SPARQL queries"
    (is (empty? (db/execute-sparql-select *db* "SELECT ?s WHERE { INVALID SPARQL }")) "Should return empty list on invalid query")))
