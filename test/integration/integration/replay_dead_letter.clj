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
            [schema.test :as schema-test]))

(schema-test/deftest replay-dead-letter
  (let [system (component/start components/system-test)
        producer (component.helper/get-component-content :rabbitmq-producer system)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)
        token (common-auth/->token fixtures.user/user-info jwt-secret)]

    (testing "that we can replay a dead-letter by it's id"
      (is (match? {:status 202}
                  (http/replay-dead-letter! (-> (http/create-dead-letter! fixtures.dead-letter/wire-dead-letter token service-fn) :body :dead-letter :id)
                                            token
                                            service-fn)))

      (testing "that when we replay a dead-letter, the message is reproduced"
        (is (match? [{:topic   :porteiro.create-contact
                      :payload {:test "ok"}}]
                    @(:produced-messages producer)))))

    (testing "that we can't replay a dead-letter that does not exists"
      (is (match? {:status 404
                   :body   {:error   "resource-not-found"
                            :message "Resource could not be found"
                            :detail  "DeadLetter Not Found"}}
                  (http/replay-dead-letter! (random-uuid)
                                            token
                                            service-fn))))
    (component/stop system)))
