(ns hkimjp.wil2.todays
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [java-time.api :as jt]
   [ring.util.response :as resp]
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
  (some? (first (ds/qq uploaded? "hkimura")))
  (ds/qq uploaded? "chatgpt")
  (some? (first (ds/qq uploaded? "chatgpt")))
  (ds/qq todays-uploads "2025-09-04")
  (def data {:login "hkimura"
             :md "# hello, World"
             :date "2025-09-04"
             :updated (jt/local-date-time)})
  (ds/put! data)
  (:md (ds/pl 1))
  :rcf)

(defn upload [request]
  (page
   [:div
    [:div.text-2xl "Upload"]
    [:form {:method "post" :action "/upload"}
     [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
      "upload"]]]))

(defn upload! [request]
  (page
   [:div "upload!"]))

(defn todays [request]
  (page
   [:div "todays"]))

(defn switch [request]
  (if (some? (first (ds/qq uploaded? (user request))))
    (resp/redirect "/wil2/todays")
    (resp/redirect "/wil2/upload")))

; (page
  ;  [:div.text-2xl "switch"
  ;   [:div [:a {:href "/wil2/upload"} "upload"]]
  ;   [:div [:a {:href "/wil2/todays"} "todays"]]]))
