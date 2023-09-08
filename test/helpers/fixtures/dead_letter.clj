(ns fixtures.dead-letter
  (:import (java.time LocalDateTime ZoneOffset)
           (java.util Date TimeZone)))


(def created-at (LocalDateTime/now (.toZoneId (TimeZone/getTimeZone "UTC"))))
(def updated-at (LocalDateTime/now (.toZoneId (TimeZone/getTimeZone "UTC"))))
(def created-at-postgresql (-> (.toInstant created-at ZoneOffset/UTC)
                               Date/from))

(def updated-at-postgresql (-> (.toInstant updated-at ZoneOffset/UTC)
                               Date/from))

(def dead-letter-id (random-uuid))
(def dropped-dead-letter-id (random-uuid))
(def processed-dead-letter-id (random-uuid))
(def dropped-dead-letter-id (random-uuid))
(def wire-dead-letter-id (str dead-letter-id))

(def internal-dead-letter
  {:dead-letter/id             dead-letter-id
   :dead-letter/service        :porteiro
   :dead-letter/topic          :porteiro.create-contact
   :dead-letter/payload        "{\"test\": \"ok\"}"
   :dead-letter/exception-info "Critical Exception (StackTrace)"
   :dead-letter/created-at     deadletter-created-at
   :dead-letter/updated-at     deadletter-updated-at
   :dead-letter/replay-count   0
   :dead-letter/status         :unprocessed})

(def internal-dropped-dead-letter
  (assoc internal-dead-letter :dead-letter/id dropped-dead-letter-id
                              :dead-letter/service :porteiro-dropped
                              :dead-letter/topic :porteiro.create-contact-dropped
                              :dead-letter/exception-info "Critical Exception (StackTrace) [Dropped]"
                              :dead-letter/status :dropped))

(def internal-processed-dead-letter
  (assoc internal-dead-letter :dead-letter/id processed-dead-letter-id
                              :dead-letter/status :processed))


(def wire-dead-letter
  {:service       "PORTEIRO"
   :topic         "SOME_TOPIC"
   :exceptionInfo "Critical Exception (StackTrace)"
   :payload       "{\"test\": \"ok\"}"})

(def postgresql-dead-letter
  {:id             dead-letter-id
   :service        "PORTEIRO"
   :topic          :porteiro.create-contact
   :payload        "{\"test\": \"ok\"}"
   :exception-info "Critical Exception (StackTrace)"
   :created-at     created-at-postgresql
   :updated-at     updated-at-postgresql
   :replay-count   0
   :status         :unprocessed})
