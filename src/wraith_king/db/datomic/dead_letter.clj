(ns wraith-king.db.datomic.dead-letter
  (:require [schema.core :as s]
            [datomic.api :as d]
            [wraith-king.models.dead-letter :as models.dead-letter]))

(s/defn insert!
  [dead-letter :- models.dead-letter/DeadLetter
   datomic]
  (d/transact datomic [dead-letter]))

(s/defn lookup :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   datomic]
  (some-> (d/q '[:find (pull ?dead-letter [*])
                 :in $ ?dead-letter-id
                 :where [?dead-letter :dead-letter/id ?dead-letter-id]] (d/db datomic) dead-letter-id)
          ffirst
          (dissoc :db/id)))

(s/defn active-dead-letters :- [models.dead-letter/DeadLetter]
  "Fetch dead-letters that are not dropped and were not successfully processed"
  [datomic]
  (some-> (d/q '[:find (pull ?dead-letter [*])
                 :in $
                 :where [?dead-letter :dead-letter/status :unprocessed]] (d/db datomic))
          (->> (mapv first))
          (->> (mapv #(dissoc % :db/id)))))
