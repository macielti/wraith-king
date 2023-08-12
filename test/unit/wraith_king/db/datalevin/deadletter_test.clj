(ns wraith-king.db.datalevin.deadletter-test
  (:require [clojure.test :refer :all]
            [datalevin.core :as d]
            [wraith-king.db.datalevin.deadletter :as database.deadletter]
            [schema.test :as s]
            [fixtures.dead-letter]
            [wraith-king.wire.datalevin.dead-letter :as wire.datalevin.dead-letter]
            [matcher-combinators.test :refer [match?]])
  (:import (java.util Date)))

(s/deftest insert-and-lookup-test
  (testing "that we can insert a deadletter entity"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri wire.datalevin.dead-letter/dead-letter-skeleton)]
      (database.deadletter/insert! fixtures.dead-letter/internal-dead-letter database-connection)
      (is (= {:dead-letter/id             fixtures.dead-letter/dead-letter-id
              :dead-letter/exception-info "Critical Exception (StackTrace)"
              :dead-letter/payload        "{\"test\": \"ok\"}"
              :dead-letter/replay-count   0
              :dead-letter/service        :porteiro
              :dead-letter/status         :unprocessed
              :dead-letter/topic          :porteiro.create-contact
              :dead-letter/created-at     fixtures.dead-letter/deadletter-created-at
              :dead-letter/updated-at     fixtures.dead-letter/deadletter-updated-at}
             (database.deadletter/lookup fixtures.dead-letter/dead-letter-id (d/db database-connection)))))))

(s/deftest active-test
  (testing "that we can query all active dead-letters"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri wire.datalevin.dead-letter/dead-letter-skeleton)]
      (database.deadletter/insert! fixtures.dead-letter/internal-dead-letter database-connection)
      (database.deadletter/insert! fixtures.dead-letter/internal-processed-dead-letter database-connection)
      (database.deadletter/insert! fixtures.dead-letter/internal-dropped-dead-letter database-connection)
      (is (= [fixtures.dead-letter/internal-dead-letter]
             (database.deadletter/active (d/db database-connection)))))))

(s/deftest mask-as-processed-test
  (testing "that we can mark a unprocessed dead-letter as processed"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri wire.datalevin.dead-letter/dead-letter-skeleton)]
      (database.deadletter/insert! fixtures.dead-letter/internal-dead-letter database-connection)
      (database.deadletter/mask-as-processed! fixtures.dead-letter/internal-dead-letter
                                              database-connection)
      (is (match? (assoc fixtures.dead-letter/internal-dead-letter
                    :dead-letter/replay-count 1
                    :dead-letter/status :processed
                    :dead-letter/updated-at (fn [update-at] (type Date) update-at))
                  (database.deadletter/lookup fixtures.dead-letter/dead-letter-id (d/db database-connection)))))))

(s/deftest mark-as-unprocessed-test
  (testing "that we can revert a dead-letter status from processed to unprocessed"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri wire.datalevin.dead-letter/dead-letter-skeleton)]
      (database.deadletter/insert! fixtures.dead-letter/internal-processed-dead-letter database-connection)
      (database.deadletter/mark-as-unprocessed! fixtures.dead-letter/processed-dead-letter-id database-connection)
      (is (match? (assoc fixtures.dead-letter/internal-processed-dead-letter
                    :dead-letter/status :unprocessed
                    :dead-letter/updated-at (fn [update-at] (type Date) update-at))
                  (database.deadletter/lookup fixtures.dead-letter/processed-dead-letter-id (d/db database-connection)))))))

(s/deftest mark-as-dropped-test
  (testing "that we can drop a unprocessed dead-letter"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri wire.datalevin.dead-letter/dead-letter-skeleton)]
      (database.deadletter/insert! fixtures.dead-letter/internal-dead-letter database-connection)
      (database.deadletter/mark-as-dropped! fixtures.dead-letter/dead-letter-id database-connection)
      (is (match? (assoc fixtures.dead-letter/internal-dead-letter
                    :dead-letter/status :dropped
                    :dead-letter/updated-at (fn [update-at] (type Date) update-at))
                  (database.deadletter/lookup fixtures.dead-letter/dead-letter-id (d/db database-connection)))))))
