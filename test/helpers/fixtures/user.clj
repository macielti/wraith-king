(ns fixtures.user
  (:require [common-clj.test.helper.core :as test.helper]))

(def user-info
  {:user {:id    (test.helper/uuid)
          :roles [:admin]}})
