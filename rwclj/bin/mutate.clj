(require '[mutant.core :as mutant]
         '[mutant.helpers.clojure-test :as clojure-test]
         '[clojure.tools.namespace.find :as find]
         '[clojure.java.io :as io])

(let [source-paths ["src"]
      test-paths ["test"]]
  (->> test-paths
       (mapcat #(find/find-namespaces-in-dir (io/file %)))
       (run! require))
  (letfn [(survivor? [prev curr]
            (not= (-> prev :survivors first)
                  (-> curr :survivors first)))
          (report [prev curr]
            (print (if (survivor? prev curr) \x \.))
            (flush)
            curr)
          (final-report [result]
            (print "\n\n")
            (mutant/pprint result)
            (flush))]
    (binding [*out* *err*]
      (->> (mutant/run source-paths
                       test-paths
                       clojure-test/test-fn)
           (reduce report)
           final-report))
    (shutdown-agents)))
