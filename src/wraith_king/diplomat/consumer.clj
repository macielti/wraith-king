(ns wraith-king.diplomat.consumer
  (:require [schema.core :as s]))

(s/defn create-contact!
  [{:keys [message]}
   {:keys [datomic]}]
  (let [dead-letter (adapters.contact/wire->internal-contact message)]
    (datomic.contact/insert! contact (:connection datomic))))
