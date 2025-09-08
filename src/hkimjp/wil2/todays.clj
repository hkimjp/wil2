(ns hkimjp.wil2.todays
  (:require
   [clojure.string :as str]
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [nextjournal.markdown :as md]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user today]]
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

(defn upload
  [request]
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
        (page
         [:div "upload success."]))
      (page
       [:div.mx-4
        [:span.text-2xl.text-red-600 "error"]
        [:br]
        [:p "did not select a file to upload."]]))))

(defn- pt [s]
  (condp = (last (str/split s #"/"))
    "good" 2
    "soso" 1
    "bad" -1))

;; ここで redis にメモる。
(defn point! [{params :params :as request}]
  (let [user (user request)
        id (parse-long (:eid params))
        pt (pt (:uri request))
        one-minute? (some? (c/get (str "wil2:" user ":pt")))
        count-range (count (c/lrange (str "wil2:" user ":" (today))))]
    (t/log! :info (str "point! " user " to " id " pt " pt))
    (t/log! :debug (str "one-minute? " one-minute?))
    (t/log! :debug (str "count-range " count-range))
    (cond
      one-minute?
      (do
        (t/log! :info "1分以内には出せない")
        (-> (resp/redirect "/wil2/todays")
            (assoc :session :flash "1分以内には出せない")))
      (< 5 count-range)
      (do
        (t/log! :info "一日5通以上出せない。")
        (-> (resp/redirect "/wil2/todays")
            (assoc :session :flash "一日5通以上出せない。")))
      :else
      (do
        (ds/put! {:wil2 "point"
                  :login user
                  :to/id id
                  :pt pt
                  :updated (jt/local-date-time)})
        (c/lpush (str "wil2:" user ":" (today)) id)
        (c/setex (str "wil2:" user ":pt") 60 id)
        (resp/redirect "/wil2/todays")))))

(defn- button [key sym]
  [:button {:hx-post (str "/wil2/point/" key)
            :hx-target "#body"
            :hx-swap "innerHTML"}
   [:span.hover:text-2xl sym]])

(defn md
  "get /wil2/md/:eid"
  [{{:keys [eid]} :path-params :as request}]
  (let [md (:md (ds/pl (parse-long eid)))
        markdown (-> md md/parse md/->hiccup)]
    (t/log! :info (str "md " (user request) " " eid))
    (resp/response
     (str (h/html
           [:form
            (h/raw (anti-forgery-field))
            [:input {:type "hidden" :name "eid" :value eid}]
            markdown
            [:div.flex.gap-x-4
             [:span.py-2.font-bold "評価: "]
             (for [[key sym] [["good" "⬆️"] ["soso" "➡️"] ["bad"  "⬇️"]]]
               (button key sym))]])))))

(defn- link [[eid login]]
  [:a.inline-block.pr-2.hover:underline
   {:hx-get (str "/wil2/md/" eid)
    :hx-target "#wil"}
   login])

(defn- filter-answered
  [uploads answered]
  (into #{}
        (for [[id user] uploads]
          (when-not (some #(= (str id) %) answered)
            [id user]))))

;; ここでフィルタする
(defn todays
  [request]
  (t/log! :debug "todays")
  (let [today (today)
        answered (c/lrange (str "wil2:" (user request) ":" today))
        uploads (ds/qq todays-uploads today)
        filtered (filter-answered uploads answered)]
    (page
     [:div.mx-4
      [:div.text-2xl.font-medium "Todays"]
      (when-let [flash (:flash request)]
        [:div {:class "text-red-500"} flash])
      [:p "他のユーザの WIL を読んで評価する。"]
      [:div.font-bold "uploaded"]
      (into [:div] (mapv link filtered))
      [:div#wil.py-2 [:span.font-bold "評価: "] " ⬆️ ➡️ ⬇️"]])))

(defn switch [request]
  (t/log! :debug "switch")
  (if (some? (first (ds/qq uploaded? (user request) (today))))
    (resp/redirect "/wil2/todays")
    (resp/redirect "/wil2/upload")))
