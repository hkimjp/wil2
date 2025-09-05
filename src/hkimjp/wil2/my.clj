(ns hkimjp.wil2.my
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(defn my [request]
  (t/log! :debug "my")
  (page
   [:div
    [:div.text-2xl (user  request) "'s points"]
    [:div
     [:div.font-bold "送信ポイント"]
     [:p "⬆️　➡️　⬇️"]
     [:div.font-bold "受信ポイント"]]]))
