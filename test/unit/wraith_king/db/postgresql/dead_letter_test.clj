(ns wraith-king.db.postgresql.dead-letter-test
  (:require [clojure.test :refer :all]
            [fixtures.dead-letter]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]
            [wraith-king.db.postgresql.dead-letter :as database.dead-letter]
            [common-clj.component.postgresql :as component.postgresql])
  (:import (org.testcontainers.containers GenericContainer)))

(s/deftest insert-test
  (let [{:keys [database-connection
                postgresql-container]} (component.postgresql/postgresql-for-unit-tests "resources/schema.sql")]
    (testing "That we can insert a dead-letter entity on database"
      (is (match? {:dead-letter/id             fixtures.dead-letter/dead-letter-id
                   :dead-letter/topic          fixtures.dead-letter/topic
                   :dead-letter/created-at     common-clj.time.core/local-datetime?
                   :dead-letter/updated-at     common-clj.time.core/local-datetime?
                   :dead-letter/exception-info fixtures.dead-letter/exception-info
                   :dead-letter/payload        fixtures.dead-letter/dead-letter-payload
                   :dead-letter/replay-count   0
                   :dead-letter/status         :unprocessed}
                  (database.dead-letter/insert! fixtures.dead-letter/internal-dead-letter database-connection))))
    (.stop ^GenericContainer postgresql-container)))

(s/deftest lookup-test
  (let [{:keys [database-connection
                postgresql-container]} (component.postgresql/postgresql-for-unit-tests "resources/schema.sql")]
    (testing "That we can query dead-letter entity by its id"

      (database.dead-letter/insert! fixtures.dead-letter/internal-dead-letter database-connection)

      (is (match? {:dead-letter/id             fixtures.dead-letter/dead-letter-id
                   :dead-letter/topic          fixtures.dead-letter/topic
                   :dead-letter/created-at     common-clj.time.core/local-datetime?
                   :dead-letter/updated-at     common-clj.time.core/local-datetime?
                   :dead-letter/exception-info fixtures.dead-letter/exception-info
                   :dead-letter/payload        fixtures.dead-letter/dead-letter-payload
                   :dead-letter/replay-count   0
                   :dead-letter/status         :unprocessed}
                  (database.dead-letter/lookup fixtures.dead-letter/dead-letter-id database-connection)))

      (is (match? nil?
                  (database.dead-letter/lookup (random-uuid) database-connection))))
    (.stop ^GenericContainer postgresql-container)))

;TODO: Check if :updated-at was really updated
(s/deftest mark-as-unprocessed-test
  (let [{:keys [database-connection
                postgresql-container]} (component.postgresql/postgresql-for-unit-tests "resources/schema.sql")]
    (testing "That we can mark a dead-letter entity as unprocessed"
      (is (match? {:dead-letter/status :processed}
                  (database.dead-letter/insert! (assoc fixtures.dead-letter/internal-dead-letter
                                                  :dead-letter/status :processed) database-connection)))
      (is (match? {:dead-letter/status :unprocessed}
                  (database.dead-letter/mark-as-unprocessed! fixtures.dead-letter/dead-letter-id database-connection))))
    (.stop ^GenericContainer postgresql-container)))
