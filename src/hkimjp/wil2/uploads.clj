(ns hkimjp.wil2.uploads
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [nextjournal.markdown :as md]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user today now]]
   [hkimjp.wil2.view :refer [page html]]))

(def max-count "submisions allowed in a day"
  (if-let [v (env :max-count)]
    (parse-long v)
    10))

(def min-interval "inteval between submissions"
  (if-let [v (env :min-interval)]
    (parse-long v)
    60))

(defn- fetch-wils
  "fetch all wils submited during last `days` days.
   (fetch-wils 1) ... fetch today's wils
   (fetch-wils 3) ... fetch wils between now and 3 days before "
  [days]
  (let [uploads-after '[:find ?e ?login
                        :in $ ?date
                        :where
                        [?e :wil2 "upload"]
                        [?e :login ?login]
                        [?e :updated ?updated]
                        [(java-time.api/after? ?updated ?date)]]]
    (ds/qq uploads-after
           (jt/minus (jt/local-date-time) (jt/days days)))))

(defn upload
  "when (env :develop) or on tuesday, "
  [request]
  (let [uploaded (fetch-wils 3)]
    (t/log! :info (str "upload " (user request)))
    (page
     [:div.mx-4
      [:div.text-2xl "Upload (" (user request) ")"]
      [:div
       [:p.py-4 "今日の WIL を提出する。"]
       [:div
        [:span.font-bold "Uploaded:"]
        [:p.m-4 (interpose " " (mapv second uploaded))]]
       [:div
        [:span.font-bold "Upload yours:"]
        [:p "今日の WIL を書いたマークダウンを選んで upload。"]
        [:form.m-4 {:method "post" :action "/wil2/upload" :enctype "multipart/form-data"}
         (h/raw (anti-forgery-field))
         [:input.border-1.rounded-md
          {:type   "file"
           :accept ".md"
           :name   "file"}]
         [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
          "upload"]]]]])))

(defn upload! [request]
  (let [user (user request)]
    (t/log! :info (str "upload! " user))
    (if-let [u (get-in request [:params :file :tempfile])]
      (do
        (ds/put! {:wil2 "upload"
                  :login user
                  :md (slurp u)
                  :date (today)
                  :updated (jt/local-date-time)})
        (resp/redirect "/wil2/rating"))
      (page
       [:div.mx-4
        [:span.text-2xl.text-red-600 "error"]
        [:br]
        [:p "did not select a file to upload."]]))))

