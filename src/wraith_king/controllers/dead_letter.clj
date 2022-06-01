(ns wraith-king.controllers.dead-letter
  (:require [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.db.datomic.dead-letter :as datomic.dead-letter]))

(s/defn create!
  [dead-letter :- models.dead-letter/DeadLetter
   datomic]
  (datomic.dead-letter/insert! dead-letter datomic))
