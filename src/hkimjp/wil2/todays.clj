(ns hkimjp.wil2.todays
  (:require
   ;;[clojure.string :as str]
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
  (t/log! :debug "upload")
  (let [uploaded (ds/qq todays-uploads (today))]
    (page
     [:div
      [:div.text-2xl "Upload (" (user request) ")"]
      [:div
       [:span.font-bold "uploaded:"]
       [:p.m-4 (interpose ", " (mapv second uploaded))]]
      [:div
       [:span.font-bold "upload your markdown"]
       [:form.m-4 {:method "post" :action "/wil2/upload" :enctype "multipart/form-data"}
        (h/raw (anti-forgery-field))
        [:input
         {:type   "file"
          :accept ".md"
          :name   "file"}]
        [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
         "upload"]]]])))

(defn upload! [request]
  (let [login (user request)
        _ (t/log! :info (get-in request [:params :file :tempfile]))]
    ;;(t/log! :info (str "upload! " login " " (abbrev md 40)))
    (try
      (ds/put! {:wil2 "upload"
                :login login
                :md (slurp (get-in request [:params :file :tempfile]))
                :date (today)
                :updated (jt/local-date-time)})
      (page [:div "upload success"])
      (catch Exception e
        (let [e (.getMessage e)]
          (t/log! :error e)
          (page [:div "error"
                 [:p e]]))))))

(defn markdown [eid]
  (t/log! :debug (:md (ds/pl eid)))
  (-> (:md (ds/pl eid))
      md/parse
      md/->hiccup
      h/html
      str))

(defn md [{{:keys [eid]} :path-params :as request}]
  (t/log! :info (str (user request) eid))
  (resp/response
   (markdown (parse-double eid))))

(defn- link [[eid login]]
  [:span.px-2 {:hx-get (str "/wil2/md/" eid)
               :hx-target "#wil"}
   login])

(defn todays [request]
  (t/log! :debug "todays")
  (let [uploads (ds/qq todays-uploads (today))]
    (page
     [:div
      [:div.text-2xl.font-medium "Todays"]
      [:div.font-bold "uploaded"]
      (into [:div.mx-2] (mapv link uploads))
      [:div#wil.mx-4 "[markdown]"]])))

(comment
  (let [uploads (ds/qq todays-uploads (today))]
    [:div
     [:div "todays"]
     (into [:div] (mapv link uploads))])
  :rcf)

(defn switch [request]
  (t/log! :debug "switch")
  (if (some? (first (ds/qq uploaded? (user request))))
    (resp/redirect "/wil2/todays")
    (resp/redirect "/wil2/upload")))
