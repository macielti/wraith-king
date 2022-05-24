(ns wraith-king.components
  (:require [com.stuartsierra.component :as component]
            [common-clj.component.config :as component.config]))


(def system
  (component/system-map
    :config (component.config/new-config "resources/config.edn" :prod :edn)))

(defn start-system! []
  (component/start system))

(def system-test
  (component/system-map
    :config (component.config/new-config "resources/config.example.edn" :test :edn)))
