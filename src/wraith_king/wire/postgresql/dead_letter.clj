(ns wraith-king.wire.postgresql.dead-letter
  (:require [camel-snake-kebab.core :as camel-snake-kebab]
            [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]))

(def statuses (->> models.dead-letter/statuses
                   (map camel-snake-kebab/->SCREAMING_SNAKE_CASE_STRING)))
(s/defschema Status (apply s/enum statuses))

(s/defschema DeadLetter
  {:id             s/Uuid
   :service        s/Str
   :topic          s/Str
   :payload        s/Str
   :exception_info s/Str
   :created_at     s/Inst
   :updated_at     s/Inst
   :replay_count   s/Int
   :status         Status})