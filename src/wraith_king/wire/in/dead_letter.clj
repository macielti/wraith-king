(ns wraith-king.wire.in.dead-letter
  (:require [schema.core :as s]))

(s/defschema DeadLetter
  {:service        s/Keyword
   :topic          s/Keyword
   :exception-info s/Str
   :payload        s/Str})
