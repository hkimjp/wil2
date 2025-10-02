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

; need generalize
(defn- can-upload?
  "today is allowed to upload wils?"
  []
  (or (env :develop) (jt/tuesday? (jt/local-date))))

(defn- can-rate?
  "in the period of rating allowed?"
  []
  (let [today (jt/local-date)]
    (or (jt/tuesday? today)
        (jt/wednesday? today)
        (jt/thursday? today))))

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
      (if (can-upload?)
        [:div
         [:p.py-4 "今日の WIL を提出する。このメニューは自分 WIL をアップロードする前しか現れない。"]
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

(defn- todays-ratings [user]
  (let [today (re-pattern (today))]
    (->> (c/lrange (format "wil2:%s" user))
         (filter (fn [d] (re-find today d))))))

(comment
  (re-pattern (today))
  (c/lrange (format "wil2:%s" "hkimura"))
  (-> (todays-ratings "hkimura")
      count)
  :rcf)

(defn point! [{params :params :as request}]
  (let [user (user request)
        id (parse-long (:eid params))
        pt (pt (:uri request))
        ;now (jt/local-date-time)
        ]
    (t/log! :info (str "point! " user " to " id " pt " pt))
    (cond
      (some? (c/get (str "wil2:" user ":pt")))
      (warn user (str min-interval "秒以内に連投できない " (now)))
      ; (< max-count (count (c/lrange (str "wil2:" user ":" (today)))))
      (< max-count (count (todays-ratings user)))
      (warn user "一日の最大可能評価数を超えた")
      :else
      (do
        (ds/put! {:wil2 "point"
                  :login user
                  :to/id id
                  :pt pt
                  :updated now})
        (c/lpush (format "wil2:%s" user) (str (jt/local-date-time))
                 (c/setex (str (format "wil2:%s:pt" user) min-interval (now))))))
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

(defn- hx-link [[eid _login]]
  [:a.inline-block.pr-2.hover:underline
   {:hx-get (str "/wil2/md/" eid)
    :hx-trigger "mouseenter"
    :hx-target "#wil"}
   ;login
   "******"])

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
                     (if-let [tm (c/get (format "wil2:%s:pt" user))]
                       tm
                       "-:-:-"))]
      (when-let [flash (:flash request)]
        [:div.text-red-500 flash])
      [:p.py-4 "他のユーザの WIL をきちんと読んで評価する。"
       [:ul
        [:li "授業当日以降3日間だけ評価できる。"]
        [:li (format "%d 秒以内に連投できない。" min-interval)]
        [:li (format "最大で %d 個しか投げられない。" max-count)]]]
      [:br]
      [:div
       [:span.font-bold "未評価 WIL: "]
       "塗りつぶしたアカウントにカーソル乗せると WIL とその下に評価ボタンを表示する。"]
      (into [:div.m-4] (mapv hx-link filtered))
      [:div#wil
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
        uploaded? (seq (ds/qq query (user request) (today)))]
    (t/log! {:level :debug :id "switch" :data {:uploaded? uploaded?}})
    (page
     (if develop?
       [:div.mx-4
        [:div.text-2xl "今週の WIL (DEVELOP)"]
        [:p "DEVELOP ではいつでも upload/rating できる。"]
        [:ul
         [:li [:a.hover:underline {:href "/wil2/upload"} "upload"]]
         [:li [:a.hover:underline {:href "/wil2/todays"} "rating"]]]]
       ; production
       [:div.mx-4
        [:div.text-2xl "今週の WIL"]
        [:p.py-2 "授業日中に今日の WIL を提出、"
         "授業後3日以内に他受講生の WIL を評価する。"
         "送信と受信の両方が平常点。"]
        [:ul
         [:li (cond
                (not (can-upload?)) [:span "WIL が提出できるのは授業のあった日。"]
                uploaded? [:span "提出済みです。"]
                :else [:a.hover:underline {:href "/wil2/upload"} "今日のWILを提出"])]
         [:li (if (can-rate?)
                [:a.hover:underline {:href "/wil2/todays"} "今週のWILを評価"]
                [:span "rating 期間終了。"])]]]))))
