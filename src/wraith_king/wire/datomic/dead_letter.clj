(ns wraith-king.wire.datomic.dead-letter)

(def dead-letter
  [{:db/ident       :dead-letter/id
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Dead-letter Id"}
   {:db/ident       :dead-letter/service
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Name of the service from where the dead-letter was raised"}
   {:db/ident       :dead-letter/topic
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Topic of the original message"}
   {:db/ident       :dead-letter/exception-info
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Stack trace from the dead-letter exception"}
   {:db/ident       :dead-letter/created-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant when the dead-letter arrived"}
   {:db/ident       :dead-letter/updated-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "Instant when the last update was made to the dead-letter entity"}
   {:db/ident       :dead-letter/replay-count
    :db/valueType   :db.type/int
    :db/cardinality :db.cardinality/one
    :db/doc         "Number of times that the dead-letter was replayed"}
   {:db/ident       :dead-letter/status
    :db/valueType   :db.type/int
    :db/cardinality :db.cardinality/one
    :db/doc         "Status of the dead-letter, if it's ':unprocessed', ':processed' or ':blocked'"}])
