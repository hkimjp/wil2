(ns hkimjp.wil2.todays
  (:require
   [ring.util.response :as response]
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(defn upload [request]
  (page [:div "upload"]))

(defn upload! [request]
  (page [:div "upload!"]))

(defn todays [request]
  (page [:div "todays"]))
