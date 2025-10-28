(ns hkimjp.wil2.weeks
  (:require
   ; [hiccup2.core :as h]
   [java-time.api :as jt]
   [nextjournal.markdown :as md]
   ; [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.wil2.view :refer [page htmx]]
   [hkimjp.datascript :as ds]))

(def dates '[:find [?date ...]
             :where
             [?e :date ?date]])

(comment
  (ds/qq dates)
  :rcf)
(defn- link [day]
  [:span.px-2.hover:underline
   {:hx-get (str "/wil2/browse/" day)
    :hx-target "#weeks"}
   day])

(defn list-days [_request]
  (page
   [:div.mx-4
    [:div.text-2xl.font-meduim "Weeks"]
    [:p.py-2 "日付をクリックでその週の WIL を表示する。"]
    (into [:div] (mapv link (sort (ds/qq dates))))
    [:br]
    [:div#weeks "[wils]"]]))

(defn browse [{{:keys [date]} :path-params}]
  (let [uploads '[:find ?author ?updated ?md
                  :in $ ?date
                  :where
                  [?e :wil2 "upload"]
                  [?e :date ?date]
                  [?e :login ?author]
                  [?e :updated ?updated]
                  [?e :md ?md]]
        [y m d] (->> (re-seq #"\d+" date)
                     (map parse-long))
        diff (jt/time-between (jt/local-date y m d) (jt/local-date) :days)
        display-author? (< 3 diff)]
    (t/log! :debug (str "browse: date " date))
    (htmx (for [[author date-time upload] (ds/qq uploads date)]
            [:div
             [:hr]
             (when display-author?
               [:div [:span.font-bold "author: "] author])
             [:div [:span.font-bold "date: "] (jt/format "YYYY-MM-dd HH:mm:ss" date-time)]
             (-> upload
                 md/parse
                 md/->hiccup)]))))
