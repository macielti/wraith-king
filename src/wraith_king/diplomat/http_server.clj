(ns wraith-king.diplomat.http-server
  (:require [wraith-king.diplomat.http-server.dead-letter :as diplomat.http-server.dead-letter]
            [wraith-king.interceptors.user-identity :as interceptors.user-identity]))

(def routes [["/api/dead-letters" :post [interceptors.user-identity/user-identity-interceptor
                                         (interceptors.user-identity/user-required-roles-interceptor [:admin])
                                         diplomat.http-server.dead-letter/create!] :route-name :create-dead-letter]
             ["/api/dead-letters/:id" :get [diplomat.http-server.dead-letter/fetch] :route-name :fetch-dead-letter-by-id]])
