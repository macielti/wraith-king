(ns wraith-king.db.datomic.dead-letter
  (:require [schema.core :as s]
            [datomic.api :as d]
            [wraith-king.models.dead-letter :as models.dead-letter]))

(s/defn insert!
  [dead-letter :- models.dead-letter/DeadLetter
   datomic]
  (d/transact datomic [dead-letter]))

(s/defn lookup-by-dead-letter-id! :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   datomic]
  (some-> (d/q '[:find (pull ?dead-letter [*])
                 :in $ ?dead-letter-id
                 :where [?dead-letter :dead-letter/id ?dead-letter-id]] (d/db datomic) dead-letter-id)
          ffirst))
