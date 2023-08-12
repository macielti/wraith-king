(ns wraith-king.db.datalevin.deadletter
  (:require [datalevin.core :as d]
            [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter])
  (:import (java.util Date)))

(s/defn insert!
  [dead-letter :- models.dead-letter/DeadLetter
   database-connection]
  (s/validate models.dead-letter/DeadLetter dead-letter)
  (d/transact database-connection [dead-letter]))

(s/defn lookup :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   database-snapshot]
  (some-> (d/q '[:find (pull ?dead-letter [*])
                 :in $ ?dead-letter-id
                 :where [?dead-letter :dead-letter/id ?dead-letter-id]] database-snapshot dead-letter-id)
          ffirst
          (dissoc :db/id)))

(s/defn active :- [models.dead-letter/DeadLetter]
  "Fetch dead-letters that are not dropped and were not successfully processed"
  [database-snapshot]
  (some-> (d/q '[:find (pull ?dead-letter [*])
                 :in $
                 :where [?dead-letter :dead-letter/status :unprocessed]] database-snapshot)
          (->> (mapv first))
          (->> (mapv #(dissoc % :db/id)))))

(s/defn mask-as-processed!
  [{:dead-letter/keys [id replay-count] :as dead-letter} :- models.dead-letter/DeadLetter
   database-connection]
  (s/validate models.dead-letter/DeadLetter dead-letter)
  (d/transact database-connection [{:dead-letter/id         id
                                    :dead-letter/updated-at (Date.)}
                                   [:db/cas [:dead-letter/id id] :dead-letter/status :unprocessed :processed]
                                   [:db/cas [:dead-letter/id id] :dead-letter/replay-count replay-count (inc replay-count)]]))

(s/defn mark-as-unprocessed!
  [dead-letter-id :- s/Uuid
   database-connection]
  (d/transact database-connection [{:dead-letter/id         dead-letter-id
                                    :dead-letter/updated-at (Date.)}
                                   [:db/cas [:dead-letter/id dead-letter-id] :dead-letter/status :processed :unprocessed]]))

(s/defn mark-as-dropped!
  [dead-letter-id :- s/Uuid
   datalevin-connection]
  (d/transact datalevin-connection [{:dead-letter/id         dead-letter-id
                                     :dead-letter/updated-at (Date.)}
                                    [:db/cas [:dead-letter/id dead-letter-id] :dead-letter/status :unprocessed :dropped]]))