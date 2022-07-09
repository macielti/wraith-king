(ns integration.fetch-dead-letter
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [wraith-king.components :as components]
            [common-clj.component.helper.core :as component.helper]
            [matcher-combinators.test :refer [match?]]
            [integration.aux.http :as http]
            [common-clj.auth.core :as common-auth]
            [common-clj.test.helper.core :as test.helper]))

(def user-info
  {:user {:id    (test.helper/uuid)
          :roles [:admin]}})

(deftest fetch-non-existent-dead-letter
  (let [system (component/start components/system-test)
        service-fn (:io.pedestal.http/service-fn (component.helper/get-component-content :service system))
        {:keys [jwt-secret]} (component.helper/get-component-content :config system)]
    (testing "that we can fetch dead-letters [not fund case]"
      (is (match? {:status 404
                   :body   "Not Found"}
                  (http/fetch-dead-letter-by-its-id (test.helper/uuid)
                                                    (common-auth/->token user-info jwt-secret)
                                                    service-fn))))
    (component/stop system)))
