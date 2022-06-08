(ns wraith-king.diplomat.http-server
  (:require [wraith-king.diplomat.http-server.dead-letter :as diplomat.http-server.dead-letter]))

(def routes [["/api/dead-letters" :post diplomat.http-server.dead-letter/create!]])
