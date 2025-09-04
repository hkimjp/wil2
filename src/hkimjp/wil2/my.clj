(ns hkimjp.wil2.my
  (:require
   [ring.util.response :as response]
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(defn my [request]
  (t/log! :debug "my")
  (page [:div "my"]))



