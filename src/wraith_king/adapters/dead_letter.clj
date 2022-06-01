(ns wraith-king.adapters.dead-letter
  (:require [schema.core :as s]
            [wraith-king.models.dead-letter :as models.dead-letter]))

(s/defn wire->dead-letter :- models.dead-letter/DeadLetter
  [])
