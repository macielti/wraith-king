(ns integration.create-dead-letter
  (:require [clojure.test :refer :all]
            [common-clj.component.rabbitmq.producer :as component.rabbitmq.producer]
            [integration.aux.http :as http]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [common-clj.auth.core :as common-auth]
            [matcher-combinators.test :refer [match?]]
            [wraith-king.components :as components]
            [clj-uuid]
            [fixtures.dead-letter]
            [fixtures.user]
            [schema.test :as s]))

(s/deftest create-dead-letter
  (let [system (component/start components/system-test)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)
        token (common-auth/->token fixtures.user/user-info jwt-secret)]

    (testing "that we can create a endpoint using a dead-letter"
      (is (match? {:status 201
                   :body   {:dead-letter {:service        "PORTEIRO"
                                          :payload        "{\"test\": \"ok\"}"
                                          :topic          "SOME_TOPIC"
                                          :status         "UNPROCESSED"
                                          :id             clj-uuid/uuid-string?
                                          :replay-count   0
                                          :exception-info "Critical Exception (StackTrace)"
                                          :updated-at     string?
                                          :created-at     string?}}}
                  (http/create-dead-letter! fixtures.dead-letter/wire-dead-letter
                                            token
                                            service-fn)))

      (testing "that creating the same dead-letter two times, only increase the replay count of the first one if it was in a processed status"
        (http/replay-dead-letter! (-> (http/fetch-active-dead-letters token service-fn) :body first :dead-letter :id)
                                  token
                                  service-fn)

        (is (match? {:status 201
                     :body   {:dead-letter {:service        "PORTEIRO"
                                            :payload        "{\"test\": \"ok\"}"
                                            :topic          "SOME_TOPIC"
                                            :status         "UNPROCESSED"
                                            :id             clj-uuid/uuid-string?
                                            :replay-count   1
                                            :exception-info "Critical Exception (StackTrace)"
                                            :updated-at     string?
                                            :created-at     string?}}}
                    (http/create-dead-letter! fixtures.dead-letter/wire-dead-letter
                                              token
                                              service-fn)))))
    (component/stop system)))

(s/deftest create-dead-letter-via-rabbitmq-message
  (let [system (component/start components/system-test)
        producer (component.helper/get-component-content :rabbitmq-producer system)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)
        token (common-auth/->token fixtures.user/user-info jwt-secret)]

    (testing "that we can create a dead-letter via rabbitmq message"
      (component.rabbitmq.producer/produce! {:topic   :create-dead-letter
                                             :payload fixtures.dead-letter/wire-dead-letter}
                                            producer)

      (Thread/sleep 5000)

      (is (match? {:status 200
                   :body   [{:dead-letter {:exception-info "Critical Exception (StackTrace)"
                                           :id             "eba6c1aa-9409-3a5d-ab2f-b4a4cc5b14b8"
                                           :payload        "{\"test\": \"ok\"}"
                                           :replay-count   0
                                           :service        "PORTEIRO"
                                           :status         "UNPROCESSED"
                                           :topic          "SOME_TOPIC"}}]}
                  (http/fetch-active-dead-letters token service-fn))))

    (component/stop system)))
