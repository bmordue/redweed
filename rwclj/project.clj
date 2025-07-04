(defproject rwclj "0.1.0-SNAPSHOT"
  :description "interact with the redweed database"
  :url "https://github.com/bmordue/redweed"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.logging "1.3.0"]
                 [org.apache.jena/jena-tdb2 "5.0.0"]
                 [clj-http "3.13.0"]
                 [metosin/jsonista "0.3.13"]
                 [ring/ring-jetty-adapter "1.14.2"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.1"]]
  :main ^:skip-aot rwclj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
