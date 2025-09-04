(ns hkimjp.wil2.todays
  (:require
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(defn upload [request]
  (page [:div "upload"]))

(defn upload! [request]
  (page [:div "upload!"]))

(defn todays [request]
  (page [:div "todays"]))

(defn switch [request]
  (page
   [:div.mx-4.text-2xl "switch"
    [:div.font-bold [:a {:href "/wil2/upload"} "upload"]]
    [:div.font-bold [:a {:href "/wil2/todays"} "todays"]]]))
