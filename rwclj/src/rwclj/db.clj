(ns rwclj.db
  (:require [clojure.tools.logging :as log])
  (:import [org.apache.jena.tdb2 TDB2Factory]
           [org.apache.jena.query QueryFactory QueryExecutionFactory]
           [org.apache.jena.rdf.model Model]))

(def ^:dynamic *dataset* nil)

(defn get-dataset []
  (let [dataset-path (or (System/getenv "JENA_DB_PATH") "data/tdb2")]
    (TDB2Factory/connectDataset dataset-path)))

(defn execute-sparql-select [query-string]
  (let [dataset (get-dataset)]
    (try
      (.begin dataset)
      (let [model (.getDefaultModel dataset)
            query (QueryFactory/create query-string)]
        (with-open [qexec (QueryExecutionFactory/create query model)]

          (let [results (.execSelect qexec)
                vars (.getResultVars results)]
            (doall
             (for [soln (iterator-seq results)]
               (reduce (fn [m v]
                         (assoc m (keyword v)
                                (when-let [node (.get soln v)]
                                  (cond
                                    (.isResource node) (.getURI node)
                                    (.isLiteral node) (.getLexicalForm node)
                                    :else (.toString node)))))
                       {} vars))))))


      (catch Exception e
        (log/error e "Error executing SPARQL query")
        [])
      (finally
        (.end dataset)
        (.close dataset)))))

(defn store-rdf-model! [dataset ^Model model]
  (let [target-model (.getDefaultModel dataset)]
    (.add target-model model)
    (log/info "Successfully stored RDF model")))

