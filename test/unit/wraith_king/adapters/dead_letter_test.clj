(ns wraith-king.adapters.dead-letter-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as st]
            [wraith-king.adapters.dead-letter :as adapters.dead-letter]))

(def wire-dead-letter
  {:service       "ednaldo-pereira-service"
   :topic         "send-email"
   :exceptionInfo "Very Strange Exception"
   :payload       "{\"test\": \"ok\"}"})

(st/deftest wire->dead-letter-test
  (testing "that we can adapt wire to a internal dead-letter entity"
    (is (match? {:dead-letter/id             uuid?
                 :dead-letter/service        :ednaldo-pereira-service
                 :dead-letter/replay-count   0
                 :dead-letter/created-at     inst?
                 :dead-letter/updated-at     inst?
                 :dead-letter/exception-info "Very Strange Exception"
                 :dead-letter/status         :unprocessed
                 :dead-letter/topic          :send-email
                 :dead-letter/payload        "{\"test\": \"ok\"}"}
                (adapters.dead-letter/wire->dead-letter wire-dead-letter)))))
