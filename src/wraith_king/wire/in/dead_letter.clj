(ns wraith-king.wire.in.dead-letter
  (:require [schema.core :as s]))

(s/defschema DeadLetter
  {:service        s/Str
   :topic          s/Str
   :exception-info s/Str})
