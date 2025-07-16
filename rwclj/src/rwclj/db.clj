(ns rwclj.db
  (:require [clojure.tools.logging :as log])
  (:import [org.apache.jena.tdb2 TDB2Factory]
           [org.apache.jena.query QueryFactory QueryExecutionFactory]
           [org.apache.jena.rdf.model Model]))

(def ^:dynamic *dataset* nil)

(defn get-dataset []
  (let [dataset-path (or (System/getenv "JENA_DB_PATH") "data/tdb2")]
    (TDB2Factory/connectDataset dataset-path)))

(defn execute-sparql-select
  ([query-string]
   (with-open [dataset (get-dataset)]
     (.begin dataset)
     (try
       (execute-sparql-select dataset query-string)
       (finally
         (.end dataset)))))
  ([dataset query-string]
   (try
     (let [model (.getDefaultModel dataset)
           query (QueryFactory/create query-string)]
       (with-open [qexec (QueryExecutionFactory/create query model)]
         (let [results (.execSelect qexec)]
           (doall
            (for [soln (iterator-seq results)]
              (reduce (fn [acc var]
                        (let [node (.get soln var)]
                          (assoc acc (keyword var)
                                 (cond
                                   (.isResource node) (.getURI node)
                                   (.isLiteral node) (.getLexicalForm node)
                                   :else (.toString node)))))
                      {} (iterator-seq (.varNames soln))))))))
     (catch Exception e
       (log/error e "Error executing SPARQL query")
       []))))

(defn store-rdf-model! [dataset ^Model model]
  (let [target-model (.getDefaultModel dataset)]
    (.add target-model model)
    (log/info "Successfully stored RDF model")))

(defn execute-sparql-update
  ([update-string]
   (with-open [dataset (get-dataset)]
     (.begin dataset)
     (try
       (execute-sparql-update dataset update-string)
       (.commit dataset)
       (finally
         (.end dataset)))))
  ([dataset update-string]
   (try
     (let [update (UpdateFactory/create update-string)]
       (with-open [qexec (UpdateExecutionFactory/create update dataset)]
         (.execute qexec)))
     (catch Exception e
       (log/error e "Error executing SPARQL update")))))
