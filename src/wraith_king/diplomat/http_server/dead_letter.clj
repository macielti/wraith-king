(ns wraith-king.diplomat.http-server.dead-letter
  (:require [schema.core :as s]
            [wraith-king.controllers.dead-letter :as controllers.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]
            [common-clj.component.postgresql :as component.postgresql])
  (:import (java.util UUID)))

(s/defn create!
  "Create new dead-letter"
  [{dead-letter          :json-params
    {:keys [postgresql]} :components}]
  (try {:status 201
        :body   {:dead-letter (-> (adapters.dead-letter/wire->dead-letter dead-letter)
                                  (controllers.dead-letter/create! (component.postgresql/get-connection postgresql))
                                  adapters.dead-letter/->wire)}}
       (catch Exception ex
         ex)))

(s/defn fetch
  [{{:keys [id]}         :path-params
    {:keys [postgresql]} :components}]
  {:status 200
   :body   {:dead-letter (-> (UUID/fromString id)
                             (controllers.dead-letter/fetch (component.postgresql/get-connection postgresql))
                             adapters.dead-letter/->wire)}})

(s/defn fetch-active
  [{{:keys [postgresql]} :components}]
  {:status 200
   :body   (-> (controllers.dead-letter/fetch-active (component.postgresql/get-connection postgresql))
               (->> (map #(do {:dead-letter (adapters.dead-letter/->wire %)}))))})

(s/defn drop!
  [{{:keys [id]}         :path-params
    {:keys [postgresql]} :components}]
  {:status 200
   :body   {:dead-letter (-> (UUID/fromString id)
                             (controllers.dead-letter/drop! (component.postgresql/get-connection postgresql))
                             adapters.dead-letter/->wire)}})

(s/defn replay!
  [{{:keys [id]}                           :path-params
    {:keys [postgresql rabbitmq-producer]} :components}]
  (try (controllers.dead-letter/replay! (UUID/fromString id)
                                        (component.postgresql/get-connection postgresql)
                                        rabbitmq-producer)
       (catch Exception ex
         #p ex))
  {:status 202})
