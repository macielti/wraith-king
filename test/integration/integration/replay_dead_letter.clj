(ns integration.replay-dead-letter
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [wraith-king.components :as components]
            [common-clj.component.helper.core :as component.helper]
            [integration.aux.http :as http]
            [common-clj.auth.core :as common-auth]
            [matcher-combinators.test :refer [match?]]
            [fixtures.dead-letter]
            [fixtures.user]
            [common-clj.component.kafka.consumer :as component.consumer]
            [schema.test :as schema-test]))

(schema-test/deftest create-dead-letter
  (let [system (component/start components/system-test)
        consumer (component.helper/get-component-content :consumer system)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)
        token (common-auth/->token fixtures.user/user-info jwt-secret)]

    (testing "that we can replay a dead-letter by it's id"
      (is (match? {:status 202}
                  (http/replay-dead-letter! (-> (http/create-dead-letter! fixtures.dead-letter/wire-dead-letter token service-fn) :body :dead-letter :id)
                                            token
                                            service-fn)))

      (testing "that when we replay a dead-letter, the message is reproduced"
        (is (match? [{:topic :some-topic
                      :data  {:payload {:test "ok"}}}]
                    (component.consumer/produced-messages consumer)))))

    (testing "that we can't replay a dead-letter that does not exists"
      (is (match? {:status 404
                   :body   {:error   "resource-not-found"
                            :message "Resource could not be found"
                            :detail  "Not Found"}}
                  (http/replay-dead-letter! (random-uuid)
                                            token
                                            service-fn))))
    (component/stop system)))
