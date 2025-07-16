  (ns mutate
    (:require [mutant :as mutant]
              [clojure.test :as clojure-test]))
  
  (binding [*out* *err*]
    (mutant/run! ["src"]
                 ["test"]
                 clojure.test/run-tests))
