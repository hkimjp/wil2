(ns hkimjp.wil2.weeks
  (:require
   [hiccup2.core :as h]
   [nextjournal.markdown :as md]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.wil2.view :refer [page]]
   [hkimjp.datascript :as ds]))

(def dates '[:find [?date ...]
             :where
             [?e :date ?date]])
; (ds/qq dates)

(def uploads '[:find [?md ...]
               :in $ ?date
               :where
               [?e :date ?date]
               [?e :md ?md]])
; (ds/qq uploads "2025-09-06")
; (count (ds/qq uploads "2025-09-04"))

(defn- link [day]
  [:span.px-2 {:hx-get (str "/wil2/browse/" day)
               :hx-target "#weeks"}
   day])

(defn list-days [request]
  (page
   [:div
    [:div.text-2xl.font-meduim "Weeks"]
    (into [:div] (mapv link (ds/qq dates)))
    [:div#weeks "[weely submissions]"]]))

(defn browse [{{:keys [date]} :path-params :as request}]
  (t/log! :debug (str "browse: date " date))
  (-> (for [upload (ds/qq uploads date)]
        (conj (-> upload
                  md/parse
                  md/->hiccup)
              [:hr]))
      h/html
      str
      resp/response))

(browse {:path-params "2025-09-05"})
