(ns wraith-king.wire.datalevin.dead-letter)

(def dead-letter-skeleton
  {:dead-letter/id {:db/valueType :db.type/uuid
                    :db/unique    :db.unique/identity}})