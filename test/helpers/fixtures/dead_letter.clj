(ns fixtures.dead-letter
  (:import (java.util Date)))

(def dead-letter-id (random-uuid))
(def wire-dead-letter-id (str dead-letter-id))

(def internal-dead-letter
  {:dead-letter/id             dead-letter-id
   :dead-letter/service        :porteiro
   :dead-letter/topic          :porteiro.create-contact
   :dead-letter/payload        "{\"test\": \"ok\"}"
   :dead-letter/exception-info "Critical Exception (StackTrace)"
   :dead-letter/created-at     (Date.)
   :dead-letter/updated-at     (Date.)
   :dead-letter/replay-count   0
   :dead-letter/status         :unprocessed})
