(defproject rwclj "0.1.0-SNAPSHOT"
  :description "interact with the redweed database"
  :url "https://github.com/bmordue/redweed"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/ring-swagger-ui "4.0.0"]]
  :main ^:skip-aot rwclj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
