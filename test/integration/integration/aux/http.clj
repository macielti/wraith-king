(ns integration.aux.http
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test]
            [cheshire.core :as json]))

(defn create-dead-letter!
  [dead-letter
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/api/dead-letters"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode dead-letter))]
    {:status status
     :body   (json/decode body true)}))
