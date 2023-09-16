(ns wraith-king.diplomat.consumer
  (:require [schema.core :as s]
            [wraith-king.wire.in.dead-letter :as wire.in.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]
            [common-clj.component.postgresql :as component.postgresql]))

(s/defn create-dead-letter!
  [{dead-letter          :payload
    {:keys [postgresql]} :components}]
  (-> (adapters.dead-letter/wire->dead-letter dead-letter)
      (controllers.dead-letter/create! (component.postgresql/get-connection postgresql))))

(def consumers
  {:create-dead-letter {:schema     wire.in.dead-letter/DeadLetter
                        :handler-fn create-dead-letter!}})
