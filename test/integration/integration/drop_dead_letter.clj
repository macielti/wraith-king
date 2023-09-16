(ns integration.drop-dead-letter
  (:require [clojure.test :refer :all]
            [wraith-king.components :as components]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [common-clj.auth.core :as common-auth]
            [integration.aux.http :as http]
            [matcher-combinators.test :refer [match?]]
            [clj-uuid]
            [fixtures.user]
            [fixtures.dead-letter]
            [schema.test :as schema-test]))

(schema-test/deftest drop-dead-letter
  (let [system (component/start components/system-test)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)
        token (common-auth/->token fixtures.user/user-info jwt-secret)
        created-dead-letter (http/create-dead-letter! fixtures.dead-letter/wire-dead-letter
                                                      token
                                                      service-fn)]
    (testing "that we can create a endpoint using a dead-letter"
      (is (match? {:status 201
                   :body   {:dead-letter {:service        "PORTEIRO"
                                          :payload        "{\"test\": \"ok\"}"
                                          :topic          "porteiro.create-contact"
                                          :status         "UNPROCESSED"
                                          :id             clj-uuid/uuid-string?
                                          :replay-count   0
                                          :exception-info "Critical Exception (StackTrace)"
                                          :updated-at     string?
                                          :created-at     string?}}}
                  created-dead-letter)))

    (testing "that we can drop a unprocessed dead-letter"
      (is (match? {:status 200
                   :body   {:dead-letter {:status         "DROPPED"
                                          :id             clj-uuid/uuid-string?
                                          :replay-count   0
                                          :exception-info "Critical Exception (StackTrace)"}}}
                  (http/drop-dead-letter! (-> created-dead-letter :body :dead-letter :id)
                                          token
                                          service-fn))))

    (testing "that we can't drop a dead-letter that does not exists"
      (is (= {:status 404
              :body   {:error   "resource-not-found",
                       :message "Resource could not be found",
                       :detail  "DeadLetter Not Found"}}
             (http/drop-dead-letter! (random-uuid)
                                     token
                                     service-fn))))

    (component/stop system)))
