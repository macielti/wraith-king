(ns wraith-king.db.datalevin.deadletter
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]))

(s/defn insert!
  [deadletter :- models.dead-letter/DeadLetter
   database-connection]
  (s/validate models.dead-letter/DeadLetter deadletter)
  (d/transact database-connection [deadletter]))

(s/defn lookup :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   database-snapshot]
  (some-> (d/q '[:find (pull ?dead-letter [*])
                 :in $ ?dead-letter-id
                 :where [?dead-letter :dead-letter/id ?dead-letter-id]] database-snapshot dead-letter-id)
          ffirst
          (dissoc :db/id)))