#_(ns wraith-king.db.datomic.config
  (:require [wraith-king.wire.datomic.dead-letter :as wire.datomic.dead-letter]))

#_(def schemas (concat []
                     wire.datomic.dead-letter/dead-letter))
