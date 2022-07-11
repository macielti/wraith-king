(ns integration.create-dead-letter
  (:require [clojure.test :refer :all]
            [integration.aux.http :as http]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [common-clj.auth.core :as common-auth]
            [matcher-combinators.test :refer [match?]]
            [wraith-king.components :as components]
            [clj-uuid]
            [fixtures.dead-letter]
            [fixtures.user]))

(deftest create-dead-letter
  (let [system (component/start components/system-test)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)]
    (testing "that we can create a endpoint using a dead-letter"
      (is (match? {:status 201
                   :body   {:service        "PORTEIRO"
                            :payload        "{\"test\": \"ok\"}"
                            :topic          "SOME_TOPIC"
                            :status         "UNPROCESSED"
                            :id             clj-uuid/uuid-string?
                            :replay-count   0
                            :exception-info "Critical Exception (StackTrace)"
                            :updated-at     string?
                            :created-at     string?}}
                  (http/create-dead-letter! fixtures.dead-letter/wire-dead-letter
                                            (common-auth/->token fixtures.user/user-info jwt-secret)
                                            service-fn))))
    (component/stop system)))
