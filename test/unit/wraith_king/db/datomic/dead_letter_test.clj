(ns wraith-king.db.datomic.dead-letter-test
  (:require [clojure.test :refer :all]
            [schema.test :as st]
            [datomic.api :as d]
            [matcher-combinators.test :refer [match?]]
            [fixtures.dead-letter]
            [wraith-king.db.datomic.dead-letter :as datomic.dead-letter]
            [common-clj.component.datomic :as component.datomic]
            [wraith-king.db.datomic.config :as datomic.config]))

(st/deftest lookup-test&insert-test
  (let [mocked-datomic (component.datomic/mocked-datomic datomic.config/schemas)]
    (datomic.dead-letter/insert! fixtures.dead-letter/internal-dead-letter mocked-datomic)
    (testing "that we can query a dead-letter by it's id"
      (is (match? fixtures.dead-letter/internal-dead-letter
                  (datomic.dead-letter/lookup fixtures.dead-letter/dead-letter-id mocked-datomic))))
    (d/release mocked-datomic)))

(deftest active-test
  (let [mocked-datomic (component.datomic/mocked-datomic datomic.config/schemas)]
    (datomic.dead-letter/insert! fixtures.dead-letter/internal-dead-letter mocked-datomic)
    (datomic.dead-letter/insert! fixtures.dead-letter/dropped-internal-dead-letter mocked-datomic)
    (testing "that we can query all active dead-letters"
      (is (match? [fixtures.dead-letter/internal-dead-letter]
                  (datomic.dead-letter/active mocked-datomic))))
    (d/release mocked-datomic)))
