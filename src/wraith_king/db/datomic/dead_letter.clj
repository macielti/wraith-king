(ns wraith-king.db.datomic.dead-letter
  (:require [schema.core :as s]
            [datomic.api :as d]
            [wraith-king.models.dead-letter :as models.dead-letter]))

(s/defn insert!
  [dead-letter :- models.dead-letter/DeadLetter
   datomic]
  (d/transact datomic [dead-letter]))
