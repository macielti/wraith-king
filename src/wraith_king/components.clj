(ns wraith-king.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]
            [common-clj.component.routes :as component.routes]
            [common-clj.component.service :as component.service]
            [common-clj.component.rabbitmq.consumer :as component.rabbitmq.consumer]
            [common-clj.component.rabbitmq.producer :as component.rabbitmq.producer]
            [common-clj.component.postgresql :as component.postgresql]
            [wraith-king.diplomat.http-server :as diplomat.http-server]
            [wraith-king.diplomat.consumer :as diplomat.consumer]))


(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)
    :postgresql (component/using (component.postgresql/new-postgreslq) [:config])
    :routes (component.routes/new-routes diplomat.http-server/routes)
    :rabbitmq-consumer (component/using (component.rabbitmq.consumer/new-consumer diplomat.consumer/consumers) [:config :postgresql])
    :rabbitmq-producer (component/using (component.rabbitmq.producer/new-producer) [:config])
    :service (component/using (common-clj.component.service/new-service) [:routes :postgresql :rabbitmq-producer :config])))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)
    :postgresql (component/using (component.postgresql/new-mock-postgresql) [:config])
    :routes (component.routes/new-routes diplomat.http-server/routes)
    :rabbitmq-consumer (component/using (component.rabbitmq.consumer/new-consumer diplomat.consumer/consumers) [:config :postgresql])
    :rabbitmq-producer (component/using (component.rabbitmq.producer/new-producer) [:config])
    :service (component/using (component.service/new-service) [:routes :postgresql :rabbitmq-producer :config])))
