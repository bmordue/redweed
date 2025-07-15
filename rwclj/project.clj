(defproject rwclj "0.1.0-SNAPSHOT"
  :description "Redweed Clojure application"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.0"]
                 [metosin/jsonista "0.3.7"]
                 [clj-http "3.12.3"]
                 [org.clojure/tools.logging "1.2.4"]
                 [ch.qos.logback/logback-classic "1.4.11"]
                 [aero "1.1.6"]
                 [org.clojure/spec.alpha "0.3.218"]
                 [metosin/ring-swagger "1.0.0"]
                 [metosin/ring-swagger-ui "5.20.0"]
                 [com.drewnoakes/metadata-extractor "2.18.0"]
                 [org.apache.jena/jena-core "4.10.0"]
                 [org.apache.jena/jena-tdb2 "4.10.0"]
                 [org.apache.jena/jena-arq "4.10.0"]
                 [org.apache.jena/jena-fuseki-main "4.10.0" :exclusions [org.eclipse.jetty/jetty-server
                                                                        org.eclipse.jetty/jetty-http
                                                                        org.eclipse.jetty/jetty-io
                                                                        org.eclipse.jetty/jetty-servlet
                                                                        org.eclipse.jetty/jetty-util]]
                 [org.apache.jena/jena-text "4.10.0"]
                 [org.apache.jena/jena-iri "4.10.0"]]
  :main rwclj.server
  :profiles {:dev {:dependencies [[nrepl "1.0.0"]
                                  [cider/cider-nrepl "0.35.0"]
                                  [org.clojure/test.check "1.1.1"]
                                  [hawk "0.2.11"]]}
             :test {:dependencies [[ring/ring-mock "0.4.0"]
                                   [org.clojure/test.check "1.1.1"]
                                   [lambdaisland/kaocha "1.87.1366"]
                                   [lambdaisland/kaocha-junit-xml "0.0.76"]]}}
  :aliases {"kaocha" ["with-profile" "+test" "run" "-m" "kaocha.runner"]})
