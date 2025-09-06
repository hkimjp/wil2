(ns hkimjp.wil2.todays
  (:require
   [clojure.string :as str]
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [nextjournal.markdown :as md]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user today abbrev]]
   [hkimjp.wil2.view :refer [page]]))

(def uploaded? '[:find ?e
                 :in $ ?login ?date
                 :where
                 [?e :wil2 "upload"]
                 [?e :login ?login]
                 [?e :date ?date]])

(def todays-uploads '[:find ?e ?login
                      :in $ ?today
                      :where
                      [?e :wil2 "upload"]
                      [?e :login ?login]
                      [?e :date ?today]])

(defn upload [request]
  (t/log! :debug "upload")
  (let [uploaded (ds/qq todays-uploads (today))]
    (page
     [:div.mx-4
      [:div.text-2xl "Upload (" (user request) ")"]
      [:p "今日の WIL を提出する。"]
      [:div
       [:span.font-bold "uploaded:"]
       [:p.m-4 (interpose ", " (mapv second uploaded))]]
      [:div
       [:span.font-bold "upload your markdown"]
       [:p "今日のWILを書いたマークダウンを選んで upload ボタン。"]
       [:form.m-4 {:method "post" :action "/wil2/upload" :enctype "multipart/form-data"}
        (h/raw (anti-forgery-field))
        [:input
         {:type   "file"
          :accept ".md"
          :name   "file"}]
        [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
         "upload"]]]])))

(defn upload! [request]
  (let [login (user request)]
    (t/log! :info (str "upload! " login))
    (if-let [u (get-in request [:params :file :tempfile])]
      (do
        (ds/put! {:wil2 "upload"
                  :login login
                  :md (slurp u)
                  :date (today)
                  :updated (jt/local-date-time)})
        (page [:div "upload success."]))
      (page [:div "did not select a file to upload."]))))

(defn- pt [s]
  (condp = (last (str/split s #"/"))
    "good" 2
    "soso" 1
    "bad" -1))

(defn point [{params :params :as request}]
  (t/log! :info "point")
  (t/log! :info (str "params " params))
  (t/log! :info (str "uri: " (:uri request)))
  (ds/put! {:wil2 "point"
            :login (user request)
            :to/id (parse-long (:eid params))
            :pt (pt (:uri request))
            :date (today)
            :updated (jt/local-date-time)})
  (resp/response "<p>received</p>"))

(defn md [{{:keys [eid]} :path-params :as request}]
  (let [md (:md (ds/pl (parse-long eid)))
        markdown (-> md
                     md/parse
                     md/->hiccup)]
    (t/log! :info (str "md " (user request) " " eid))
    (resp/response
     (str (h/html
           [:form
            (h/raw (anti-forgery-field))
            [:input {:type "hidden" :name "eid" :value eid}]
            markdown
            [:div.flex.gap-x-4
             [:span.py-2.font-bold "評価: "]
             [:button {:hx-post "/wil2/point/good"
                       :hx-target "#wil"}
              [:span.hover:text-2xl "⬆️"]]
             [:button {:hx-post "/wil2/point/soso"
                       :hx-target "#wil"}
              [:span.hover:text-2xl "➡️"]]
             [:button {:hx-post "/wil2/point/bad"
                       :hx-target "#wil"}
              [:span.hover:text-2xl "⬇️"]]]])))))

(defn- link [[eid login]]
  [:span.px-2.hover:underline
   {:hx-get (str "/wil2/md/" eid)
    :hx-target "#wil"}
   login])

(defn todays [request]
  (t/log! :debug "todays")
  (let [uploads (ds/qq todays-uploads (today))]
    (page
     [:div.mx-4
      [:div.text-2xl.font-medium "Todays"]
      [:p "他のユーザの WIL を読んで評価する。"]
      [:div.font-bold "uploaded"]
      (into [:div.mx-2] (mapv link uploads))
      [:div#wil.py-2 [:span.font-bold "評価:"] "　⬆️　➡️　⬇️"]])))

(defn switch [request]
  (t/log! :debug "switch")
  (if (some? (first (ds/qq uploaded? (user request) (today))))
    (resp/redirect "/wil2/todays")
    (resp/redirect "/wil2/upload")))
