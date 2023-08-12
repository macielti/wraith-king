(ns wraith-king.controllers.dead-letter
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.db.datalevin.deadletter :as database.dead-letter]
            [wraith-king.diplomat.producer :as diplomat.producer]))

(s/defn create! :- models.dead-letter/DeadLetter
  [{:dead-letter/keys [id] :as dead-letter} :- models.dead-letter/DeadLetter
   database-connection]
  (let [database-snapshot (d/db database-connection)]
    (if (= :processed (-> (database.dead-letter/lookup id database-snapshot) :dead-letter/status))
      (do (database.dead-letter/mark-as-unprocessed! id database-connection)
          (database.dead-letter/lookup id database-snapshot))
      (do (database.dead-letter/insert! dead-letter database-connection)
          dead-letter))))

(s/defn fetch :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   database-connection]
  (database.dead-letter/lookup dead-letter-id (d/db database-connection)))

(s/defn fetch-active :- [models.dead-letter/DeadLetter]
  [database-connection]
  (database.dead-letter/active (d/db database-connection)))

(s/defn drop! :- models.dead-letter/DeadLetter
  [dead-letter-id :- s/Uuid
   datomic]
  (datomic.dead-letter/mark-as-dropped! dead-letter-id datomic)
  (datomic.dead-letter/lookup dead-letter-id datomic))

(s/defn replay!
  [dead-letter-id :- s/Uuid
   datomic
   producer]
  (let [dead-letter (datomic.dead-letter/lookup dead-letter-id datomic)]
    (datomic.dead-letter/mask-as-processed! dead-letter datomic)
    (diplomat.producer/replay-dead-letter! dead-letter producer)))
