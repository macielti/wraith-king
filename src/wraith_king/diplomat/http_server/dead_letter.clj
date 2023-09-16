(ns wraith-king.diplomat.http-server.dead-letter
  (:require [schema.core :as s]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter])
  (:import (java.util UUID)))

(s/defn create!
  "Create new dead-letter"
  [{dead-letter          :json-params
    {:keys [postgresql]} :components}]
  {:status 201
   :body   {:dead-letter (-> (adapters.dead-letter/wire->dead-letter dead-letter)
                             (controllers.dead-letter/create! postgresql)
                             adapters.dead-letter/->wire)}})

(s/defn fetch
  [{{:keys [id]}        :path-params
    {:keys [datalevin]} :components}]
  {:status 200
   :body   {:dead-letter (-> (UUID/fromString id)
                             (controllers.dead-letter/fetch datalevin)
                             adapters.dead-letter/->wire)}})

(s/defn fetch-active
  [{{:keys [datalevin]} :components}]
  {:status 200
   :body   (-> (controllers.dead-letter/fetch-active datalevin)
               (->> (map #(do {:dead-letter (adapters.dead-letter/->wire %)}))))})

(s/defn drop!
  [{{:keys [id]}        :path-params
    {:keys [datalevin]} :components}]
  {:status 200
   :body   {:dead-letter (-> (UUID/fromString id)
                             (controllers.dead-letter/drop! datalevin)
                             adapters.dead-letter/->wire)}})

(s/defn replay!
  [{{:keys [id]}                          :path-params
    {:keys [datalevin rabbitmq-producer]} :components}]
  (controllers.dead-letter/replay! (UUID/fromString id) datalevin rabbitmq-producer)
  {:status 202})
