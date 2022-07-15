(ns wraith-king.diplomat.producer
  (:require [common-clj.component.kafka.producer :as kafka.producer]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [schema.core :as s]
            [cheshire.core :as json]))

(s/defn replay-dead-letter!
  [{:dead-letter/keys [topic payload]} :- models.dead-letter/DeadLetter
   producer]
  (kafka.producer/produce! {:topic topic
                            :data  {:payload (json/decode payload true)}}
                           producer))
