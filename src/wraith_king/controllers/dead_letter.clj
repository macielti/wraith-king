(ns wraith-king.controllers.dead-letter
  (:require [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.db.postgresql.dead-letter :as database.dead-letter]
            [wraith-king.diplomat.producer :as diplomat.producer]))

(s/defn create! :- models.dead-letter/DeadLetter
  [{:dead-letter/keys [id] :as dead-letter} :- models.dead-letter/DeadLetter
   database-connection]
  (if (= :processed (-> (database.dead-letter/lookup id database-connection) :dead-letter/status))
    (database.dead-letter/mark-as-unprocessed! id database-connection)
    (database.dead-letter/insert! dead-letter database-connection)))

(s/defn fetch :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   database-connection]
  (database.dead-letter/lookup dead-letter-id database-connection))

(s/defn fetch-active :- [models.dead-letter/DeadLetter]
  [database-connection]
  (database.dead-letter/active database-connection))

(s/defn drop! :- models.dead-letter/DeadLetter
  [dead-letter-id :- s/Uuid
   database-connection]
  (database.dead-letter/mark-as-dropped! dead-letter-id database-connection))

(s/defn replay!
  [dead-letter-id :- s/Uuid
   database-connection
   producer]
  (let [dead-letter (database.dead-letter/lookup dead-letter-id database-connection)]
    (database.dead-letter/mask-as-processed! dead-letter database-connection)
    (diplomat.producer/replay-dead-letter! dead-letter producer)))
