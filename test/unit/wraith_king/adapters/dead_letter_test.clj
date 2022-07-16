(ns wraith-king.adapters.dead-letter-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [fixtures.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]
            [fixtures.dead-letter]
            [schema.test :as schema-test]))

(schema-test/deftest wire->dead-letter-test
  (testing "that we can adapt wire to a internal dead-letter entity"
    (is (match? {:dead-letter/id             uuid?
                 :dead-letter/service        :porteiro
                 :dead-letter/replay-count   0
                 :dead-letter/created-at     inst?
                 :dead-letter/updated-at     inst?
                 :dead-letter/exception-info "Critical Exception (StackTrace)"
                 :dead-letter/status         :unprocessed
                 :dead-letter/topic          :some-topic
                 :dead-letter/payload        "{\"test\": \"ok\"}"}
                (adapters.dead-letter/wire->dead-letter fixtures.dead-letter/wire-dead-letter))))

  (testing "the same wire dead-letter input always produce dead-letters with the same id"
    (is (match? {:dead-letter/id #uuid "eba6c1aa-9409-3a5d-ab2f-b4a4cc5b14b8"}
                (adapters.dead-letter/wire->dead-letter fixtures.dead-letter/wire-dead-letter)))))

(schema-test/deftest ->wire-test
  (testing "that we can externalize a internal dead-letter entity"
    (is (match? {:id             fixtures.dead-letter/wire-dead-letter-id
                 :service        "PORTEIRO"
                 :payload        "{\"test\": \"ok\"}"
                 :exception-info "Critical Exception (StackTrace)"
                 :topic          "PORTEIRO.CREATE_CONTACT"
                 :status         "UNPROCESSED"
                 :replay-count   0
                 :updated-at     string?
                 :created-at     string?}
                (adapters.dead-letter/->wire fixtures.dead-letter/internal-dead-letter)))))
