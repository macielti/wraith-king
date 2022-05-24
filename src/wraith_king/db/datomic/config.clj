(ns wraith-king.db.datomic.config
  (:require [wraith-king.wire.datomic.dead-letter :as wire.datomic.dead-letter]))

(def schemas (concat []
                     wire.datomic.dead-letter/dead-letter))
