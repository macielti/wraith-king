(ns wraith-king.db.postgresql.dead-letter
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
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