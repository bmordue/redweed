  (binding [*out* *err*]
    (mutant/run! source-paths
                 test-paths
                 clojure-test/test-fn))
