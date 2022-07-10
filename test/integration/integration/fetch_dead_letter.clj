(ns integration.fetch-dead-letter
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [wraith-king.components :as components]
            [common-clj.component.helper.core :as component.helper]
            [matcher-combinators.test :refer [match?]]
            [integration.aux.http :as http]
            [common-clj.auth.core :as common-auth]
            [common-clj.test.helper.core :as test.helper]
            [clj-uuid]))

(def wire-dead-letter
  {:service       "SOME_SERVICE"
   :topic         "SOME_TOPIC"
   :exceptionInfo "Very strange Exception with StackTrace"
   :payload       "{\"test\": \"ok\"}"})

(def user-info
  {:user {:id    (test.helper/uuid)
          :roles [:admin]}})

(deftest fetch-non-existent-dead-letter
  (let [system (component/start components/system-test)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)
        token (common-auth/->token user-info jwt-secret)]
    (testing "that we can fetch dead-letters [not fund case]"
      (is (match? {:status 404
                   :body   "Not Found"}
                  (http/fetch-dead-letter-by-its-id (test.helper/uuid)
                                                    (common-auth/->token user-info jwt-secret)
                                                    service-fn))))
    (testing "that we can fetch dead-letters by it's id"
      (is (match? {:status 200
                   :body   {:service        "SOME_SERVICE"
                            :payload        "{\"test\": \"ok\"}"
                            :topic          "SOME_TOPIC"
                            :status         "UNPROCESSED"
                            :id             clj-uuid/uuid-string?
                            :replay-count   0
                            :exception-info "Very strange Exception with StackTrace"
                            :updated-at     string?
                            :created-at     string?}}
                  (http/fetch-dead-letter-by-its-id (-> (http/create-dead-letter! wire-dead-letter token service-fn) :body :id)
                                                    token
                                                    service-fn))))
    (component/stop system)))
