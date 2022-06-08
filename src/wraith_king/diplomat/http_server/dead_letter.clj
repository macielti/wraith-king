(ns wraith-king.diplomat.http-server.dead-letter
  (:require [schema.core :as s]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]))

(s/defn create!
  "Create new dead-letter"
  [{dead-letter       :json-params
    {:keys [datomic]} :components}]
  {:status 200
   :body   (-> (adapters.dead-letter/wire->dead-letter dead-letter)
               (controllers.dead-letter/create! datomic))})
