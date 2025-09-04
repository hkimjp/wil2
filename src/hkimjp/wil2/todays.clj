(ns hkimjp.wil2.todays
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.view :refer [page]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]))

(def uploaded? '[:find ?e
                 :in $ ?login
                 :where
                 [?e :login ?login]])

(def todays-uploads '[:find ?e ?login
                      :in $ ?today
                      :where
                      [?e :login ?login]
                      [?e :date ?today]])

(comment
  (ds/qq uploaded? "hkimura")
  (ds/qq todays-uploads "2025-09-04")
  (def data {:login "hkimura"
             :md "# hello, World"
             :date "2025-09-04"
             :updated (jt/local-date-time)})
  (ds/put! data)
  :rcf)

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
