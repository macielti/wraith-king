(ns wraith-king.adapters.dead-letter
  (:require [schema.core :as s]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.wire.in.dead-letter :as wire.in.dead-letter]
            [wraith-king.wire.out.dead-letter :as wire.out.dead-letter]
            [wraith-king.wire.postgresql.dead-letter :as wire.postgresql.dead-letter])
  (:import (java.util UUID Date)))

(s/defn wire->dead-letter :- models.dead-letter/DeadLetter
  [{:keys [service topic exceptionInfo payload]} :- wire.in.dead-letter/DeadLetter]
  {:dead-letter/id             (UUID/nameUUIDFromBytes (.getBytes (str service topic exceptionInfo payload)))
   :dead-letter/service        (camel-snake-kebab/->kebab-case-keyword service)
   :dead-letter/topic          (camel-snake-kebab/->kebab-case-keyword topic)
   :dead-letter/exception-info exceptionInfo
   :dead-letter/payload        payload
   :dead-letter/status         :unprocessed
   :dead-letter/replay-count   0
   :dead-letter/created-at     (Date.)
   :dead-letter/updated-at     (Date.)})

(s/defn ->wire :- wire.out.dead-letter/DeadLetter
  [{:dead-letter/keys [id service topic exception-info payload replay-count status created-at updated-at]} :- models.dead-letter/DeadLetter]
  {:id             (str id)
   :service        (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING service)
   :topic          (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING topic)
   :exception-info exception-info
   :payload        payload
   :replay-count   replay-count
   :status         (camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING status)
   :created-at     (str created-at)
   :updated-at     (str updated-at)})

(s/defn postgresql->internal :- models.dead-letter/DeadLetter
  [dead-letter :- wire.postgresql.dead-letter/DeadLetter]
  {:dead-letter/id             (:id dead-letter)
   :dead-letter/service        (-> dead-letter :service camel-snake-kebab/->kebab-case-keyword)
   :dead-letter/topic          (-> dead-letter :topic camel-snake-kebab/->kebab-case-keyword)
   :dead-letter/payload        (:payload dead-letter)
   :dead-letter/exception-info (:exception_info dead-letter)
   :dead-letter/created-at     (:created_at dead-letter)
   :dead-letter/updated-at     (:updated_at dead-letter)
   :dead-letter/replay-count   (:replay_count dead-letter)
   :dead-letter/status         (-> dead-letter :status camel-snake-kebab/->kebab-case-keyword)})
