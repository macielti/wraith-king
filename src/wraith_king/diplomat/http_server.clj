(ns wraith-king.diplomat.http-server
  (:require [wraith-king.diplomat.http-server.dead-letter :as diplomat.http-server.dead-letter]
            [wraith-king.interceptors.user-identity :as interceptors.user-identity]))

(def routes [["/api/dead-letters" :post [diplomat.http-server.dead-letter/create!
                                         (interceptors.user-identity/user-required-roles-interceptor [:admin])]]
             ["/api/dead-letters/:id" :get diplomat.http-server.dead-letter/fetch]])
