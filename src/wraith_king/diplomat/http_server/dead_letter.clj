(ns wraith-king.diplomat.http-server.dead-letter
  (:require [schema.core :as s]))

(s/defn create!
  "Create new dead-letter"
  [{dead-letter                       :json-params
    {:keys [datomic producer config]} :components}]
  {:status 200
   :body   nil})
