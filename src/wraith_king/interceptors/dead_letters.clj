(ns wraith-king.interceptors.dead-letters
  (:require [common-clj.error.core :as common-error]
            [wraith-king.db.postgresql.dead-letter :as database.dead-letter]
            [common-clj.component.postgresql :as component.postgresql])
  (:import (java.util UUID)))

(def resource-existence-interceptor-check
  {:name  ::resource-existence-check-interceptor
   :enter (fn [{{{:keys [id]}         :path-params
                 {:keys [postgresql]} :components} :request :as context}]
            (when-not (database.dead-letter/lookup (UUID/fromString id) (component.postgresql/get-connection postgresql))
              (common-error/http-friendly-exception 404
                                                    "resource-not-found"
                                                    "Resource could not be found"
                                                    "DeadLetter Not Found"))
            context)})
