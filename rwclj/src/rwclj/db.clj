(ns rwclj.db
  (:require [clojure.tools.logging :as log])
  (:import [org.apache.jena.tdb2 TDB2Factory]
           [org.apache.jena.query QueryFactory QueryExecutionFactory]
           [org.apache.jena.rdf.model Model]))

(def ^:dynamic *dataset* nil)

(defn get-dataset []
  (let [dataset-path (or (System/getenv "JENA_DB_PATH") "data/tdb2")]
    (TDB2Factory/connectDataset dataset-path)))

(defn execute-sparql-select [dataset query-string]
  (try
    (let [model (.getDefaultModel dataset)
          query (QueryFactory/create query-string)
          qexec (QueryExecutionFactory/create query model)
          results (.execSelect qexec)
          result-list (atom [])]
      (while (.hasNext results)
        (let [soln (.nextSolution results)
              vars (.varNames soln)
              row (reduce (fn [acc var]
                            (let [node (.get soln var)]
                              (assoc acc (keyword var)
                                     (cond
                                       (.isResource node) (.getURI node)
                                       (.isLiteral node) (.getLexicalForm node)
                                       :else (.toString node)))))
                          {} vars)]
        (swap! result-list conj row)))
      (.close qexec)
      @result-list)
    (catch Exception e
      (log/error e "Error executing SPARQL query")
      [])))

(defn store-rdf-model! [dataset ^Model model]
  (let [target-model (.getDefaultModel dataset)]
    (.add target-model model)
    (log/info "Successfully stored RDF model")))



