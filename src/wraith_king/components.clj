(ns wraith-king.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datomic :as component.datomic]
            [common-clj.component.routes :as component.routes]
            [wraith-king.db.datomic.config :as datomic.config]))


(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :datomic (component/using (component.datomic/new-datomic datomic.config/schemas) [:config])
    :routes (component/using (component.routes/new-routes diplomat.http-server/routes) [:datomic :config])))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)
    :datomic (component/using (component.datomic/new-datomic datomic.config/schemas) [:config])))
