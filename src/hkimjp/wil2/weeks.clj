(ns hkimjp.wil2.weeks
  (:require
   [ring.util.response :as response]
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(defn list-days [request]
  (page [:div "list-days"]))

(defn browse [request]
  (page [:div "browse"]))
