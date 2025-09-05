(ns hkimjp.wil2.my
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(def sent '[:find ?e ?pt
            :in $ ?login
            :where
            [?e :wil2 "point"]
            [?e :login ?login]
            [?e :pt ?pt]])

(def sent-pt '[:find ?e
               :in $ ?login ?pt
               :where
               [?e :wil2 "point"]
               [?e :login ?login]
               [?e :pt ?pt]])

(defn- ct [user pt]
  (count (ds/qq sent-pt user pt)))

(defn my [request]
  (let [user (user request)]
    (t/log! :info (str "my " user))
    (page
     [:div
      [:div.text-2xl user "'s points"]
      [:div
       [:div.font-bold "送信ポイント"]
       [:div.mx-4
        "⬆️ " (ct user 2)
        ", ➡️ " (ct user 1)
        ", ⬇️ " (ct user -1)]
       [:div.font-bold "受信ポイント"]]])))
