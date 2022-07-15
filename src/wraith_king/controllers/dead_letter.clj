(ns wraith-king.controllers.dead-letter
  (:require [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.db.datomic.dead-letter :as datomic.dead-letter]
            [wraith-king.diplomat.producer :as diplomat.producer]))

(s/defn create! :- models.dead-letter/DeadLetter
  [{:dead-letter/keys [id] :as dead-letter} :- models.dead-letter/DeadLetter
   datomic]
  (if (datomic.dead-letter/lookup id datomic)
    (datomic.dead-letter/mark-as-unprocessed! id datomic)
    (datomic.dead-letter/insert! dead-letter datomic))
  dead-letter)

(s/defn fetch :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   datomic]
  (datomic.dead-letter/lookup dead-letter-id datomic))

(s/defn fetch-active :- [models.dead-letter/DeadLetter]
  [datomic]
  (datomic.dead-letter/active datomic))

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
