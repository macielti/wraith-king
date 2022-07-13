(ns integration.aux.http
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test]
            [cheshire.core :as json]))

(defn create-dead-letter!
  [dead-letter
   token
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/api/dead-letters"
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)}
                                                 :body (json/encode dead-letter))]
    {:status status
     :body   (json/decode body true)}))

(defn fetch-dead-letter-by-its-id
  [dead-letter-id
   token
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :get (str "/api/dead-letters/" dead-letter-id)
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)})]
    {:status status
     :body   (if (= status 200)
               (json/decode body true)
               body)}))

(defn drop-dead-letter!
  [dead-letter-id
   token
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :delete (str "/api/dead-letters/" dead-letter-id)
                                                 :headers {"Content-Type"  "application/json"
                                                           "Authorization" (str "Bearer " token)})]
    {:status status
     :body   (if (= status 200)
               (json/decode body true)
               body)}))
