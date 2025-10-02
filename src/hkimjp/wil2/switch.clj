(ns hkimjp.wil2.switch
  (:require
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user today]]
   [hkimjp.wil2.view :refer [page]]))

(defn- can-upload?
  "today is allowed to upload wils?"
  []
  (or (env :develop) (jt/tuesday? (jt/local-date))))

(defn- can-rate?
  "in the period of rating allowed?"
  []
  (let [today (jt/local-date)]
    (or (jt/tuesday? today)
        (jt/wednesday? today)
        (jt/thursday? today))))

(defn switch [request]
  (let [develop? (some? (env :develop))
        query '[:find ?e
                :in $ ?who ?date
                :where
                [?e :wil2  "upload"]
                [?e :login ?who]
                [?e :date  ?date]]
        uploaded? (seq (ds/qq query (user request) (today)))]
    (t/log! {:level :debug :id "switch" :data {:uploaded? uploaded?}})
    (page
     [:div.mx-4
      [:div.text-2xl "今週の WIL " (when develop? "(DEVELOP)")]
      [:p.py-2 "授業日中に今日の WIL を提出、授業後3日以内に他受講生の WIL を評価する。"]
      (if develop?
        [:ul
         [:li [:a.hover:underline {:href "/wil2/upload"} "今日のWILを提出"]]
         [:li [:a.hover:underline {:href "/wil2/rating"} "今週のWILを評価"]]]
        [:ul
         [:li (cond
                (not (can-upload?)) [:span "WIL が提出できるのは授業のあった日。"]
                uploaded?           [:span "提出済みです。"]
                :else               [:a.hover:underline
                                     {:href "/wil2/upload"} "今日のWILを提出"])]
         [:li (if (can-rate?)
                [:a.hover:underline {:href "/wil2/rating"} "今週のWILを評価"]
                [:span "rating 期間終了。"])]])])))

