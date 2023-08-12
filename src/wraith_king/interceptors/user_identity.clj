(ns wraith-king.interceptors.user-identity
  (:require [schema.core :as s]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [clojure.string :as str]
            [common-clj.error.core :as common-error]
            [buddy.sign.jwt :as jwt]
            [common-clj.io.interceptors.datalevin :as io.interceptors.datalevin])
  (:import (java.util UUID)
           (clojure.lang ExceptionInfo)))

(s/defschema UserIdentity
  {:user-identity/id    s/Uuid
   :user-identity/roles [s/Keyword]})

(s/defn ^:private wire-jwt->user-identity :- UserIdentity
  [jwt-wire :- s/Str
   jwt-secret :- s/Str]
  (try (let [{:keys [id roles]} (:user (jwt/unsign jwt-wire jwt-secret))]
         {:user-identity/id    (UUID/fromString id)
          :user-identity/roles (map camel-snake-kebab/->kebab-case-keyword roles)})
       (catch ExceptionInfo _ (throw (ex-info "Invalid JWT"
                                              {:status 422
                                               :cause  "Invalid JWT"})))))

(def user-identity-interceptor
  {:name  ::user-identity-interceptor
   :enter (fn [{{{:keys [config]} :components
                 headers          :headers} :request :as context}]
            (assoc-in context [:request :user-identity]
                      (try (let [jw-token (-> (get headers "authorization") (str/split #" ") last)]
                             (wire-jwt->user-identity jw-token (:jwt-secret config)))
                           (catch Exception _ (common-error/http-friendly-exception 422
                                                                                    "invalid-jwt"
                                                                                    "Invalid JWT"
                                                                                    "Invalid JWT")))))})

(s/defn user-required-roles-interceptor
  [required-roles :- [s/Keyword]]
  {:name  ::user-required-roles-interceptor
   :enter (fn [{{{user-roles :user-identity/roles} :user-identity} :request :as context}]
            (if (empty? (clojure.set/difference (set required-roles) (set user-roles)))
              context
              (common-error/http-friendly-exception 403
                                                    "insufficient-roles"
                                                    "Insufficient privileges/roles/permission"
                                                    "Insufficient privileges/roles/permission")))})

(defn dead-letter-resource-identifier-fn
  [{{:keys [path-params]} :request}]
  (-> path-params :id UUID/fromString))

(def resource-existence-interceptor-check
  (io.interceptors.datalevin/resource-existence-check-interceptor dead-letter-resource-identifier-fn
                                                                  '[:find (pull ?resource [*])
                                                                    :in $ ?resource-identifier
                                                                    :where [?resource :dead-letter/id ?resource-identifier]]))
