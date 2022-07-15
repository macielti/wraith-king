(ns wraith-king.diplomat.consumer
  (:require [schema.core :as s]
            [wraith-king.wire.in.dead-letter :as wire.in.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]))

(s/defn create-dead-letter!
  [dead-letter
   {:keys [datomic]}]
  (-> (adapters.dead-letter/wire->dead-letter dead-letter)
      (controllers.dead-letter/create! (:connection datomic))))

(def consumers
  {:create-dead-letter {:schema  wire.in.dead-letter/DeadLetter
                        :handler create-dead-letter!}})
