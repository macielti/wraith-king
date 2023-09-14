(ns wraith-king.models.dead-letter
  (:require [schema.core :as s])
  (:import (java.time LocalDateTime)))

(def statuses [:unprocessed :processed :dropped])

(def Status (apply s/enum statuses))

(s/defschema DeadLetter
  {:dead-letter/id             s/Uuid
   :dead-letter/service        s/Str
   :dead-letter/topic          s/Str
   :dead-letter/payload        s/Str
   :dead-letter/exception-info s/Str
   :dead-letter/created-at     LocalDateTime
   :dead-letter/updated-at     LocalDateTime
   :dead-letter/replay-count   s/Int
   :dead-letter/status         Status})
