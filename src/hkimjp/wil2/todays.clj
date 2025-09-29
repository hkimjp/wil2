(ns hkimjp.wil2.todays
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
      [:div.text-2xl "Submit (" (user request) ")"]
      (if (or (some? (env :develop)) (jt/tuesday? (jt/local-date)))
        [:div
         [:p.py-4 "今日の WIL を提出する。このメニューは自分 WIL をアップロードする前しか現れない。"]
         [:div
          [:span.font-bold "Uploaded:"]
          [:p.m-4 (interpose " " (mapv second uploaded))]]
         [:div
          [:span.font-bold "Upload yours:"]
          [:p "今日の WIL を書いたマークダウンを選んで upload ボタン。"]
          [:form.m-4 {:method "post" :action "/wil2/upload" :enctype "multipart/form-data"}
           (h/raw (anti-forgery-field))
           [:input.border-1.rounded-md
            {:type   "file"
             :accept ".md"
             :name   "file"}]
           [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
            "upload"]]]]
        [:div "今日は授業日じゃありません。"])])))

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
        (resp/redirect "/wil2/todays"))
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

(defn- warn [user msg]
  (t/log! :warn (str "point! error " msg))
  (c/setex (str "wil2:" user ":error") 1 msg))

(defn point! [{params :params :as request}]
  (let [user (user request)
        id (parse-long (:eid params))
        pt (pt (:uri request))]
    (t/log! :info (str "point! " user " to " id " pt " pt))
    (cond
      (some? (c/get (str "wil2:" user ":pt")))
      (warn user (str min-interval "秒以内に連投できない " (now)))
      (< max-count (count (c/lrange (str "wil2:" user ":" (today)))))
      (warn user "一日の最大可能評価数を超えた")
      :else
      (do
        (ds/put! {:wil2 "point"
                  :login user
                  :to/id id
                  :pt pt
                  :updated (jt/local-date-time)})
        (c/lpush (str "wil2:" user) id)
        (c/setex (str "wil2:" user ":pt") min-interval (now))))
    (resp/redirect "/wil2/todays")))

(defn md
  "called by `get /wil2/md/:eid`"
  [{{:keys [eid]} :path-params :as request}]
  (let [user (user request)]
    (t/log! :info (str "md " user " " eid))
    (html
     [:form
      (h/raw (anti-forgery-field))
      [:input {:type "hidden" :name "eid" :value eid}]
      (-> (:md (ds/pl (parse-long eid)))
          md/parse
          md/->hiccup)
      [:div.flex.gap-x-4
       [:span.py-2.font-bold "評価: "]
       (for [[key sym] [["good" "⬆️"] ["soso" "➡️"] ["bad"  "⬇️"]]]
         [:button {:hx-post   (str "/wil2/point/" key)
                   :hx-target "#body"
                   :hx-swap   "innerHTML"
                   :hx-boost  "false"}
          [:span.hover:text-2xl sym]])]])))

;------------------------

(defn- hx-link [[eid login]]
  [:a.inline-block.pr-2.hover:underline
   {:hx-get (str "/wil2/md/" eid)
    :hx-target "#wil"}
   login])

; rating
(defn todays
  [request]
  (let [user (user request)
        uploads (fetch-wils 3)
        answered (->> (c/lrange (format "wil2:%s" user))
                      (map parse-long)
                      set)
        filtered (remove (fn [[eid _]] (answered eid)) uploads)]
    (t/log! {:level :info :id "todays" :msg user})
    (page
     [:div.mx-4
      [:div.inline-block
       [:span.text-2xl.font-medium "Rating "]]
      [:span (format "(今日の評価数: %d 最終評価時刻: %s)"
                     (count answered)
                     (c/get (format "wil2:%s:pt" user)))]
      (when-let [flash (:flash request)]
        [:div.text-red-500 flash])
      [:p.py-4 "他のユーザの WIL をきちんと読んで評価する。"
       [:ul
        [:li "授業当日以降3日間だけ評価できる。(動作未確認）"]
        [:li (format "%d 秒以内に連投できない。" min-interval)]
        [:li (format "最大で %d 個しか投げられない。" max-count)]]]
      [:br]
      [:div
       [:span.font-bold "未評価 WIL:"]
       "アカウントをクリックするとWILと評価ボタン"]
      (into [:div] (mapv hx-link filtered))
      [:div#wil.py-2
       (when-let [err (c/get (format "wil2:%s:error" user))]
         [:span.text-red-600 err])]])))

(defn switch [request]
  (let [develop? (some? (env :develop))
        query '[:find ?e
                :in $ ?who ?date
                :where
                [?e :wil2  "upload"]
                [?e :login ?who]
                [?e :date  ?date]]
        uploaded? (ds/qq query (user request) (today))]
    (t/log! {:level :debug :id "switch" :data {:uploaded? uploaded?}})
    (page
     (if develop?
       [:div.mx-4
        [:div.text-2xl "今週の WIL (DEVELOP)"]
        [:ul
         [:li [:a.hover:underline {:href "/wil2/upload"} "submit"]]
         [:li [:a.hover:underline {:href "/wil2/todays"} "rating"]]]]
       [:div.mx-4
        [:div.text-2xl "今週の WIL"]
        [:ul
         [:li (if uploaded?
                [:p "提出済みです。"]
                [:a.hover:underline {:href "/wil2/upload"} "submit"])]
         [:li [:a.hover:underline {:href "/wil2/todays"} "rating"]]]]))))



