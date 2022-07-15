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

(deftest mask-as-processed!-test
  (let [mocked-datomic (component.datomic/mocked-datomic datomic.config/schemas)]
    (datomic.dead-letter/insert! fixtures.dead-letter/internal-dead-letter mocked-datomic)
    (datomic.dead-letter/mask-as-processed! fixtures.dead-letter/internal-dead-letter mocked-datomic)
    (testing "that we can mark a dead-letter as processed incrementing the replay count"
      (is (match? {:dead-letter/id           fixtures.dead-letter/dead-letter-id
                   :dead-letter/replay-count 1
                   :dead-letter/status       :processed}
                  (datomic.dead-letter/lookup fixtures.dead-letter/dead-letter-id mocked-datomic))))
    (d/release mocked-datomic)))

(deftest mark-as-dropped!-test
  (let [mocked-datomic (component.datomic/mocked-datomic datomic.config/schemas)]
    (datomic.dead-letter/insert! fixtures.dead-letter/internal-dead-letter mocked-datomic)
    (datomic.dead-letter/mark-as-dropped! fixtures.dead-letter/dead-letter-id mocked-datomic)
    (testing "that we can query all active dead-letters"
      (is (match? {:dead-letter/id     fixtures.dead-letter/dead-letter-id
                   :dead-letter/status :dropped}
                  (datomic.dead-letter/lookup fixtures.dead-letter/dead-letter-id mocked-datomic))))
    (d/release mocked-datomic)))

(deftest mark-as-unprocessed!-test
  (let [mocked-datomic (component.datomic/mocked-datomic datomic.config/schemas)]

    (datomic.dead-letter/insert! fixtures.dead-letter/internal-dead-letter mocked-datomic)

    (testing "that the dead-letter is in an processed status"
      (datomic.dead-letter/mask-as-processed! fixtures.dead-letter/internal-dead-letter mocked-datomic)
      (is (match? {:dead-letter/id           fixtures.dead-letter/dead-letter-id
                   :dead-letter/status       :processed
                   :dead-letter/replay-count 1}
                  (datomic.dead-letter/lookup fixtures.dead-letter/dead-letter-id mocked-datomic)))

      (testing "that we can mark a processed dead-letter as unprocessed"
        (datomic.dead-letter/mark-as-unprocessed! fixtures.dead-letter/dead-letter-id mocked-datomic)
        (is (match? {:dead-letter/id           fixtures.dead-letter/dead-letter-id
                     :dead-letter/status       :unprocessed
                     :dead-letter/replay-count 1}
                    (datomic.dead-letter/lookup fixtures.dead-letter/dead-letter-id mocked-datomic)))))
    (d/release mocked-datomic)))
