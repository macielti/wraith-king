(ns fixtures.dead-letter
  (:require [common-clj.time.core :as time]))


(def created-at (time/now))
(def updated-at (time/now))
(def created-at-postgresql (time/local-datetime->date created-at))
(def updated-at-postgresql (time/local-datetime->date updated-at))
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
   :dead-letter/created-at     created-at
   :dead-letter/updated-at     updated-at
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
   :service        "porteiro"
   :topic          "porteiro.create-contact"
   :payload        "{\"test\": \"ok\"}"
   :exception_info "Critical Exception (StackTrace)"
   :created_at     created-at-postgresql
   :updated_at     updated-at-postgresql
   :replay_count   0
   :status         "UNPROCESSED"})
