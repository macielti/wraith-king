(defproject wraith-king "0.1.0-SNAPSHOT"

  :description "DLQ service"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-cloverage "1.2.3"]
            [lein-environ "1.2.0"]]

  :exclusions [log4j]

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [danlentz/clj-uuid "0.1.9"]
                 [buddy/buddy-sign "3.4.333"]
                 [com.stuartsierra/component "1.1.0"]
                 [cheshire "5.11.0"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [nubank/matcher-combinators "3.5.0"]
                 [prismatic/schema "1.2.1"]
                 [net.clojars.macielti/common-clj "12.15.12"]]

  :resource-paths ["resources"]

  :profiles {:test {:env {:clj-env "test"}}}

  :repl-options {:init-ns wraith-king.components}

  :test-paths ["test/unit" "test/integration" "test/helpers"]

  :main wraith-king.components/start-system!)
