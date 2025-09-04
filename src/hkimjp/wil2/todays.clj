(ns hkimjp.wil2.todays
  (:require
   [hkimjp.datascript :as ds]
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
   [:div.text-2xl "switch"
    [:div [:a {:href "/wil2/upload"} "upload"]]
    [:div [:a {:href "/wil2/todays"} "todays"]]]))
