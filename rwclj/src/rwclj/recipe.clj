(ns rwclj.recipe
  (:require [clj-tagsoup.parser :as parser]
            [clojure.data.zip.xml :as zx]
            [clojure.zip :as zip]))

(defn- zip-root [html-string]
  (zip/xml-zip (parser/parse-string html-string)))

(defn- text-from-selector [root selector]
  (zx/xml1-> root selector zx/text))

(defn- texts-from-selector [root selector]
  (zx/xml-> root selector zx/text))

(defn parse-hrecipe [html-string]
  (let [root (zip-root html-string)
        recipe-node (zx/xml1-> root :hrecipe)]
    {:name (text-from-selector recipe-node :fn)
     :ingredients (texts-from-selector recipe-node :ingredient)
     :instructions (text-from-selector recipe-node :instructions)}))

(def recipe-ns "http://localhost:8080/recipes/")

(defn- recipe-uri [recipe-name]
  (str recipe-ns (java.net.URLEncoder/encode recipe-name "UTF-8")))

(defn recipe->rdf [recipe]
  (let [model (org.apache.jena.rdf.model.ModelFactory/createDefaultModel)
        recipe-uri (recipe-uri (:name recipe))
        recipe-resource (.createResource model recipe-uri)]
    (.addProperty recipe-resource org.apache.jena.vocabulary.RDF/type org.apache.jena.vocabulary.RDFS/Resource)
    (.addProperty recipe-resource org.apache.jena.vocabulary.RDF/type (org.apache.jena.rdf.model.ResourceFactory/createResource "http://schema.org/Recipe"))
    (.addProperty recipe-resource (org.apache.jena.rdf.model.ResourceFactory/createProperty "http://schema.org/name") (:name recipe))
    (doseq [ingredient (:ingredients recipe)]
      (.addProperty recipe-resource (org.apache.jena.rdf.model.ResourceFactory/createProperty "http://schema.org/recipeIngredient") ingredient))
    (.addProperty recipe-resource (org.apache.jena.rdf.model.ResourceFactory/createProperty "http://schema.org/recipeInstructions") (:instructions recipe))
    model))
