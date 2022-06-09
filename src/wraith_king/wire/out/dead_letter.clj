(ns wraith-king.wire.out.dead-letter
  (:require [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]
            [camel-snake-kebab.core :as camel-snake-kebab]))

(def Status (->> models.dead-letter/statuses
                 (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)
                 (apply s/enum)))

(s/defschema DeadLetter
  {:id             s/Str
   :service        s/Str
   :topic          s/Str
   :payload        s/Str
   :exception-info s/Str
   :replay-count   s/Int
   :status         Status
   :created-at     s/Str
   :updated-at     s/Str})
