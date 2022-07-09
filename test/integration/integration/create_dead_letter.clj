(ns integration.create-dead-letter
  (:require [clojure.test :refer :all]
            [integration.aux.http :as http]
            [com.stuartsierra.component :as component]
            [common-clj.component.helper.core :as component.helper]
            [matcher-combinators.test :refer [match?]]
            [wraith-king.components :as components]
            [clj-uuid]))

(def wire-dead-letter
  {:service       "SOME_SERVICE"
   :topic         "SOME_TOPIC"
   :exceptionInfo "Very strange Exception with StackTrace"
   :payload       "{\"test\": \"ok\"}"})

(deftest create-dead-letter
  (let [system (component/start components/system-test)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))]
    (testing "that we can create a endpoint using a dead-letter"
      (is (match? {:status 201
                   :body   {:service        "SOME_SERVICE"
                            :payload        "{\"test\": \"ok\"}"
                            :topic          "SOME_TOPIC"
                            :status         "UNPROCESSED"
                            :id             clj-uuid/uuid-string?
                            :replay-count   0
                            :exception-info "Very strange Exception with StackTrace"
                            :updated-at     string?
                            :created-at     string?}}
                  (http/create-dead-letter! wire-dead-letter
                                            service-fn))))
    (component/stop system)))
