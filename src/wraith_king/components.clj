(ns wraith-king.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datomic :as component.datomic]
            [common-clj.component.routes :as component.routes]
            [common-clj.component.service :as component.service]
            [common-clj.component.kafka.consumer :as component.consumer]
            [common-clj.component.kafka.producer :as component.producer]
            [wraith-king.db.datomic.config :as datomic.config]
            [wraith-king.diplomat.http-server :as diplomat.http-server]
            [wraith-king.diplomat.consumer :as diplomat.consumer]))


(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :datomic (component/using (component.datomic/new-datomic datomic.config/schemas) [:config])
    :routes (component/using (component.routes/new-routes diplomat.http-server/routes) [:datomic :config])
    :consumer (component/using (component.consumer/new-consumer diplomat.consumer/consumers) [:config :datomic])
    :producer (component/using (component.producer/new-producer) [:config])
    :service (component/using (common-clj.component.service/new-service) [:routes :datomic :producer :config])))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)
    :datomic (component/using (component.datomic/new-datomic datomic.config/schemas) [:config])
    :routes (component/using (component.routes/new-routes diplomat.http-server/routes) [:datomic :config])
    :consumer (component/using (component.consumer/new-mock-consumer diplomat.consumer/consumers) [:config :datomic])
    :producer (component/using (component.producer/new-mock-producer) [:consumer :config])
    :service (component/using (component.service/new-service) [:routes :datomic :producer :config])))
