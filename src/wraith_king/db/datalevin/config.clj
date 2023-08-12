(ns wraith-king.db.datalevin.config
  (:require [wraith-king.wire.datalevin.dead-letter :as wire.datalevin.dead-letter]))

(def schema (merge {}
                   wire.datalevin.dead-letter/dead-letter-skeleton))