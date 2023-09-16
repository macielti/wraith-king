(ns wraith-king.diplomat.producer
  (:require [cheshire.core :as json]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [common-clj.component.rabbitmq.producer :as rabbitmq.producer]
            [schema.core :as s]))

(s/defn replay-dead-letter!
  [{:dead-letter/keys [topic payload]} :- models.dead-letter/DeadLetter
   producer]
  (rabbitmq.producer/produce! {:topic   (keyword topic)
                               :payload (json/decode payload true)}
                              producer))
