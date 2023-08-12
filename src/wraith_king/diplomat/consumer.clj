(ns wraith-king.diplomat.consumer
  (:require [schema.core :as s]
            [wraith-king.wire.in.dead-letter :as wire.in.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]))

(s/defn create-dead-letter!
  [{dead-letter         :payload
    {:keys [datalevin]} :components}]
  (-> (adapters.dead-letter/wire->dead-letter dead-letter)
      (controllers.dead-letter/create! datalevin)))

(def consumers
  {:create-dead-letter {#_:schema     #_wire.in.dead-letter/DeadLetter
                        :handler-fn create-dead-letter!}})
