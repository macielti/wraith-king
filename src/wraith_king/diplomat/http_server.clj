(ns wraith-king.diplomat.http-server
  (:require [wraith-king.diplomat.http-server.dead-letter :as diplomat.http-server.dead-letter]
            [wraith-king.interceptors.user-identity :as interceptors.user-identity]))

(def routes [["/api/dead-letters" :post [diplomat.http-server.dead-letter/create!
                                         #_(interceptors.user-identity/user-required-roles-interceptor [:admin])] :route-name :create-dead-letter]
             ["/api/dead-letters/:id" :get [diplomat.http-server.dead-letter/fetch] :route-name :fetch-dead-letter-by-id]])
