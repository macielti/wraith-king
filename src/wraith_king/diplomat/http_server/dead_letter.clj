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
   :body   {:dead-letter (-> (adapters.dead-letter/wire->dead-letter dead-letter)
                             (controllers.dead-letter/create! (:connection datomic))
                             adapters.dead-letter/->wire)}})

(s/defn fetch
  [{{:keys [id]}      :path-params
    {:keys [datomic]} :components}]
  (let [dead-letter (-> (UUID/fromString id)
                        (controllers.dead-letter/fetch (:connection datomic)))]
    (if dead-letter
      {:status 200
       :body   {:dead-letter (adapters.dead-letter/->wire dead-letter)}}
      {:status 404
       :body   "Not Found"})))

(s/defn fetch-active
  [{{:keys [datomic]} :components}]
  {:status 200
   :body   (-> (controllers.dead-letter/fetch-active (:connection datomic))
               (->> (map #(do {:dead-letter (adapters.dead-letter/->wire %)}))))})

(s/defn drop!
  [{{:keys [id]}      :path-params
    {:keys [datomic]} :components}]
  {:status 200
   :body   {:dead-letter (-> (UUID/fromString id)
                             (controllers.dead-letter/drop! (:connection datomic))
                             adapters.dead-letter/->wire)}})

(s/defn replay!
  [{{:keys [id]}               :path-params
    {:keys [datomic producer]} :components}]
  (controllers.dead-letter/replay! (UUID/fromString id) (:connection datomic) producer)
  {:status 202})
