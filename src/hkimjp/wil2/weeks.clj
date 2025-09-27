(ns hkimjp.wil2.weeks
  (:require
   ; [hiccup2.core :as h]
   [nextjournal.markdown :as md]
   ; [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.wil2.view :refer [page html]]
   [hkimjp.datascript :as ds]))

(def dates '[:find [?date ...]
             :where
             [?e :date ?date]])

(def uploads '[:find [?md ...]
               :in $ ?date
               :where
               [?e :date ?date]
               [?e :md ?md]])

(defn- link [day]
  [:span.px-2.hover:underline
   {:hx-get (str "/wil2/browse/" day)
    :hx-target "#weeks"}
   day])

(defn list-days [_request]
  (page
   [:div.mx-4
    [:div.text-2xl.font-meduim "Weeks"]
    [:p.py-2 "日付をクリックでその日の WIL を表示する。"]
    (into [:div] (mapv link (sort (ds/qq dates))))
    [:div#weeks "[wils]"]]))

(defn browse [{{:keys [date]} :path-params}]
  (t/log! :debug (str "browse: date " date))
  (html (for [upload (ds/qq uploads date)]
          (conj (-> upload
                    md/parse
                    md/->hiccup)
                [:hr]))))
