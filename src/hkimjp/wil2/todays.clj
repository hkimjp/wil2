(ns hkimjp.wil2.todays
  (:require
   ;;[clojure.string :as str]
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
      [:div.text-2xl "Upload (" (user request) ")"]
      [:div
       [:span.font-bold "uploaded:"]
       [:p.m-4 (interpose ", " (mapv second uploaded))]]
      [:div
       [:span.font-bold "upload your markdown"]
       [:form.m-4 {:method "post" :action "/wil2/upload"}
        (h/raw (anti-forgery-field))
        [:input
         {:type   "file"
          :accept ".md"
          :name   "file"}]
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


