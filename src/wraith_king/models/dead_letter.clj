(ns wraith-king.models.dead-letter
  (:require [schema.core :as s])
  (:import (java.util Date)))

(def Status (s/enum :waiting :processed :dropped))

(s/defschema DeadLetter
  {:dead-letter/id             s/Uuid
   :dead-letter/service        s/Keyword
   :dead-letter/topic          s/Keyword
   :dead-letter/exception-info s/Str
   :dead-letter/created-at     Date
   :dead-letter/updated-at     Date
   :dead-letter/replay-count   s/Int
   :dead-letter/status         Status})
