(ns hkimjp.wil2.todays
  (:require
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user today]]
   [hkimjp.wil2.view :refer [page]]))

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
  (def data [{:login "hkimura"
              :md "# hello, World"
              :date "2025-09-04"
              :updated (jt/local-date-time)}
             {:login "akari"
              :md "# hello, akari"
              :date "2025-09-04"
              :updated (jt/local-date-time)}])

  (ds/puts! data)
  (ds/qq uploaded? "hkimura")
  (some? (first (ds/qq uploaded? "hkimura")))
  (ds/qq uploaded? "akari")
  (some? (first (ds/qq uploaded? "chatgpt")))
  (ds/qq todays-uploads "2025-09-04")
  (map second (ds/qq todays-uploads "2025-09-04"))
  :rcf)

(defn upload [request]
  (let [uploaded (ds/qq todays-uploads (today))]
    (page
     [:div
      [:div.text-2xl "Upload"]
      [:div
       (str (mapv second uploaded))]
      [:div.m-4
       [:form {:method "post" :action "/upload"}
        (h/raw (anti-forgery-field))
        [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
         "upload"]]]])))

(defn upload! [request]
  (page
   [:div "upload!"]))

(defn todays [request]
  (let [uploaded (ds/qq todays-uploads (today))]
    (page
     [:div
      [:div "todays"]
      (str (mapv second uploaded))])))

(defn switch [request]
  (if (some? (first (ds/qq uploaded? (user request))))
    (resp/redirect "/wil2/todays")
    (resp/redirect "/wil2/upload")))


