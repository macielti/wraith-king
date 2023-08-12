(ns wraith-king.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.datomic :as component.datomic]
            [common-clj.component.routes :as component.routes]
            [common-clj.component.service :as component.service]
            [common-clj.component.datalevin :as component.datalevin]
            [common-clj.component.rabbitmq.consumer :as component.rabbitmq.consumer]
            [common-clj.component.rabbitmq.producer :as component.rabbitmq.producer]
            [wraith-king.db.datomic.config :as datomic.config]
            [wraith-king.diplomat.http-server :as diplomat.http-server]
            [wraith-king.diplomat.consumer :as diplomat.consumer]
            [wraith-king.db.datalevin.config :as database.config]))


(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :datalevin (component/using (component.datalevin/new-datalevin database.config/schema) [:config])
    :routes (component.routes/new-routes diplomat.http-server/routes)
    :rabbitmq-consumer (component/using (component.rabbitmq.consumer/new-consumer diplomat.consumer/consumers) [:config :datalevin])
    :rabbitmq-producer (component/using (component.rabbitmq.producer/new-producer) [:config])
    :service (component/using (common-clj.component.service/new-service) [:routes :datalevin :rabbitmq-producer :config])))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)
    :datalevin (component/using (component.datalevin/new-datalevin database.config/schema) [:config])
    :routes (component.routes/new-routes diplomat.http-server/routes)
    :rabbitmq-consumer (component/using (component.rabbitmq.consumer/new-consumer diplomat.consumer/consumers) [:config :datalevin])
    :rabbitmq-producer (component/using (component.rabbitmq.producer/new-producer) [:config])
    :service (component/using (component.service/new-service) [:routes :datalevin :rabbitmq-producer :config])))
