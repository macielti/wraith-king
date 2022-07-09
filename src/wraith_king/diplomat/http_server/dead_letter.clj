(ns wraith-king.diplomat.http-server.dead-letter
  (:require [schema.core :as s]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter])
  (:import (java.util UUID)))

(s/defn create!
  "Create new dead-letter"
  [{dead-letter       :json-params
    {:keys [datomic]} :components}]
  {:status 201
   :body   (-> (adapters.dead-letter/wire->dead-letter dead-letter)
               (controllers.dead-letter/create! (:connection datomic))
               adapters.dead-letter/->wire)})

(s/defn fetch
  [{{:keys [id]}      :path-params
    {:keys [datomic]} :components}]
  {:status 200
   :body   (-> (UUID/fromString id)
               (controllers.dead-letter/fetch (:connection datomic))
               adapters.dead-letter/->wire)})
