(ns fixtures.dead-letter
  (:import (java.util Date)))

(def deadletter-id (random-uuid))
(def deadletter-created-at (Date.))
(def deadletter-updated-at (Date.))
(def dropped-dead-letter-id (random-uuid))
(def wire-dead-letter-id (str deadletter-id))

(def internal-dead-letter
  {:dead-letter/id             deadletter-id
   :dead-letter/service        :porteiro
   :dead-letter/topic          :porteiro.create-contact
   :dead-letter/payload        "{\"test\": \"ok\"}"
   :dead-letter/exception-info "Critical Exception (StackTrace)"
   :dead-letter/created-at     deadletter-created-at
   :dead-letter/updated-at     deadletter-updated-at
   :dead-letter/replay-count   0
   :dead-letter/status         :unprocessed})

(def dropped-internal-dead-letter
  {:dead-letter/id             dropped-dead-letter-id
   :dead-letter/service        :porteiro-dropped
   :dead-letter/topic          :porteiro.create-contact-dropped
   :dead-letter/payload        "{\"test\": \"ok\"}"
   :dead-letter/exception-info "Critical Exception (StackTrace) [Dropped]"
   :dead-letter/created-at     (Date.)
   :dead-letter/updated-at     (Date.)
   :dead-letter/replay-count   0
   :dead-letter/status         :dropped})

(def wire-dead-letter
  {:service       "PORTEIRO"
   :topic         "SOME_TOPIC"
   :exceptionInfo "Critical Exception (StackTrace)"
   :payload       "{\"test\": \"ok\"}"})
