(ns wraith-king.db.datalevin.deadletter-test
  (:require [clojure.test :refer :all]
            [datalevin.core :as d]
            [wraith-king.db.datalevin.deadletter :as database.deadletter]
            [schema.test :as s]
            [fixtures.dead-letter]))

(s/deftest insert-test
  (testing "that we can insert a deadletter entity"
    (let [database-uri (datalevin.util/tmp-dir (str "query-or-" (random-uuid)))
          database-connection (d/get-conn database-uri)]
      (database.deadletter/insert! fixtures.dead-letter/internal-dead-letter database-connection)
      (is (= {:dead-letter/id             fixtures.dead-letter/deadletter-id
              :dead-letter/exception-info "Critical Exception (StackTrace)"
              :dead-letter/payload        "{\"test\": \"ok\"}"
              :dead-letter/replay-count   0
              :dead-letter/service        :porteiro
              :dead-letter/status         :unprocessed
              :dead-letter/topic          :porteiro.create-contact
              :dead-letter/created-at     fixtures.dead-letter/deadletter-created-at
              :dead-letter/updated-at     fixtures.dead-letter/deadletter-updated-at}
             (database.deadletter/lookup fixtures.dead-letter/deadletter-id (d/db database-connection)))))))
