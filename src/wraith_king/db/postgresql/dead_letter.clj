(ns wraith-king.db.postgresql.dead-letter
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [common-clj.time.core :as time]
            [next.jdbc :as jdbc]
            [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]))

(s/defn insert!
  [{:dead-letter/keys [id service topic payload exception-info created-at updated-at replay-count status]} :- models.dead-letter/DeadLetter
   database-connection]
  (let [status' (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING status)]
    (-> (jdbc/execute-one! database-connection
                           ["INSERT INTO dead_letter (id, service, topic, payload, exception_info, created_at, updated_at, replay_count, status)
                           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                            id service topic payload exception-info created-at updated-at replay-count status']
                           {:return-keys true})
        adapters.dead-letter/postgresql->internal)))

(s/defn lookup :- (s/maybe models.dead-letter/DeadLetter)
  [dead-letter-id :- s/Uuid
   database-connection]
  (some-> (jdbc/execute-one! database-connection
                             ["SELECT
                                 id,
                                 service,
                                 topic,
                                 payload,
                                 exception_info,
                                 created_at,
                                 updated_at,
                                 replay_count,
                                 status
                              FROM dead_letter
                              WHERE
                                id = ?" dead-letter-id])
          adapters.dead-letter/postgresql->internal))

(s/defn mark-as-unprocessed!
  [dead-letter-id :- s/Uuid
   database-connection]
  (-> (jdbc/execute-one! database-connection
                         ["UPDATE dead_letter
                           SET
                             status = ?,
                             updated_at = ?
                           WHERE id = ?"
                          "UNPROCESSED" (time/now) dead-letter-id]
                         {:return-keys true})
      adapters.dead-letter/postgresql->internal))

(s/defn active :- [models.dead-letter/DeadLetter]
  "Fetch dead-letters that are not dropped and were not successfully processed"
  [database-connection]
  (some->> (jdbc/execute! database-connection
                          ["SELECT
                                 id,
                                 service,
                                 topic,
                                 payload,
                                 exception_info,
                                 created_at,
                                 updated_at,
                                 replay_count,
                                 status
                           FROM dead_letter
                           WHERE
                             status = ?"
                           "UNPROCESSED"])
           (mapv adapters.dead-letter/postgresql->internal)))
