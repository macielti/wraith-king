(ns wraith-king.adapters.dead-letter
  (:require [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [wraith-king.wire.in.dead-letter :as wire.in.dead-letter]
            [camel-snake-kebab.core :as camel-snake-kebab])
  (:import (java.util UUID Date)))

;TODO: Add unit tests for that adapter
(s/defn wire->dead-letter :- models.dead-letter/DeadLetter
  [{:keys [service topic exceptionInfo payload]} :- wire.in.dead-letter/DeadLetter]
  {:dead-letter/id             (UUID/randomUUID)
   :dead-letter/service        (camel-snake-kebab/->kebab-case-keyword service)
   :dead-letter/topic          (camel-snake-kebab/->kebab-case-keyword topic)
   :dead-letter/exception-info exceptionInfo
   :dead-letter/payload        payload
   :dead-letter/status         :unprocessed
   :dead-letter/replay-count   0
   :dead-letter/created-at     (Date.)
   :dead-letter/updated-at     (Date.)})
