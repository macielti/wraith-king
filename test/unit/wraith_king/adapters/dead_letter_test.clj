(ns wraith-king.adapters.dead-letter-test
  (:require [clojure.test :refer :all]
            [common-clj.time.core :as time]
            [matcher-combinators.test :refer [match?]]
            [fixtures.dead-letter]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]
            [fixtures.dead-letter]
            [schema.test :as s]))

(s/deftest wire->dead-letter-test
  (testing "that we can adapt wire to a internal dead-letter entity"
    (is (match? {:dead-letter/id             uuid?
                 :dead-letter/service        :porteiro
                 :dead-letter/replay-count   0
                 :dead-letter/created-at     time/local-datetime?
                 :dead-letter/updated-at     time/local-datetime?
                 :dead-letter/exception-info "Critical Exception (StackTrace)"
                 :dead-letter/status         :unprocessed
                 :dead-letter/topic          :some-topic
                 :dead-letter/payload        "{\"test\": \"ok\"}"}
                (adapters.dead-letter/wire->dead-letter fixtures.dead-letter/wire-dead-letter))))

  (testing "the same wire dead-letter input always produce dead-letters with the same id"
    (is (match? {:dead-letter/id #uuid "eba6c1aa-9409-3a5d-ab2f-b4a4cc5b14b8"}
                (adapters.dead-letter/wire->dead-letter fixtures.dead-letter/wire-dead-letter)))))

(s/deftest ->wire-test
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

(s/deftest postgresql->internal-test
  (testing "that we can internalize a dead-letter from postgresql wire"
    (is (match? {:dead-letter/id             fixtures.dead-letter/dead-letter-id
                 :dead-letter/topic          "porteiro.create-contact"
                 :dead-letter/replay-count   0
                 :dead-letter/created-at     common-clj.time.core/local-datetime?
                 :dead-letter/updated-at     common-clj.time.core/local-datetime?
                 :dead-letter/exception-info "Critical Exception (StackTrace)"
                 :dead-letter/payload        "{\"test\": \"ok\"}"
                 :dead-letter/service        "porteiro"
                 :dead-letter/status         :unprocessed}
                (adapters.dead-letter/postgresql->internal fixtures.dead-letter/postgresql-dead-letter)))))
