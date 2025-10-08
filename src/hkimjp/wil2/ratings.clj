(ns hkimjp.wil2.ratings
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
   [hkimjp.wil2.util :refer [user today local-time]]
   [hkimjp.wil2.view :refer [page html]]))

; refer from admin.clj. must be public.
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

(defn- pt [s]
  (condp = (last (str/split s #"/"))
    "good" 2
    "soso" 1
    "bad" -1))

(defn- warn [user msg]
  (t/log! :warn (str "point! error " msg))
  (c/setex (str "wil2:" user ":error") 1 msg))

(defn- count-todays-ratings [user]
  (c/llen (format "wil2:%s:%s" user (today))))

(defn- update-todays-ratings [user time]
  (c/lpush (format "wil2:%s:%s" user (today)) time))

(defn- last-rating-time [user]
  (first (c/lrange (format "wil2:%s:%s" user (today)))))

(defn- in-interval? [user]
  (some? (c/get (format "wil2:%s" user))))

(defn- update-interval [user]
  (c/setex (format "wil2:%s" user) min-interval 1)) ; better value than 1?

(defn- answered [user]
  (c/lrange (format "wil2:%s:answerd" user)))

(defn- update-answered [user id]
  (c/lpush (format "wil2:%s:answerd" user) id))

(defn point! [{params :params :as request}]
  (let [user (user request)
        id (parse-long (:eid params))
        pt (pt (:uri request))
        local-date-time (jt/local-date-time)
        now (local-time)]
    (t/log! {:level :info :id "point!"})
    (cond
      (in-interval? user)
      (warn user (str min-interval "秒以内に連投できない " now))
      (<= max-count (count-todays-ratings user))
      (warn user (format "一日の最大可能評価数 %d を超えた" max-count))
      :else
      (do
        (ds/put! {:wil2 "point"
                  :login user
                  :to/id id
                  :pt pt
                  :updated local-date-time})
        (update-interval user)
        (update-todays-ratings user now)
        (update-answered user id)))
    (resp/redirect "/wil2/rating")))

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
    :hx-trigger "click"
    :hx-target "#wil"}
   (if (env :develop) login "******")])

(defn rating
  [request]
  (let [user (user request)
        uploads (fetch-wils 3)
        answered (->> (answered user) (map parse-long) set)
        filtered (remove (fn [[eid _]] (answered eid)) uploads)]
    (t/log! {:level :info :id "rating" :data {:filtered filtered}})
    (page
     [:div.mx-4
      [:div.inline-block
       [:span.text-2xl.font-medium "Rating: " user]
       [:span.mx-2]
       [:span (format "(今日の評価数: %d 最終評価時刻: %s)"
                      (count-todays-ratings user)
                      (if-let [tm (last-rating-time user)] tm "-:-:-"))]]
      (when-let [flash (:flash request)]
        [:div.text-red-500 flash])
      [:p.py-4 "他のユーザの WIL をきちんと読んで評価する。"
       [:ul
        [:li "授業当日以降3日間だけ評価できる。"]
        [:li (format "%d 秒以内に連投できない。" min-interval)]
        [:li (format "最大で %d 個しか投げられない。" max-count)]]]
      [:br]
      [:div
       [:span.font-bold (format "未評価 WIL(%d): " (count filtered))]
       "塗りつぶしたアカウントをクリックで WIL とその下に評価ボタンを表示する。"]
      (when (some? (env :develop))
        [:p "(本番では塗りつぶす。開発中に **** だとちょっと面倒だ。)"])
      (into [:div.m-4] (mapv hx-link filtered))
      [:div#wil
       (when-let [err (c/get (format "wil2:%s:error" user))]
         [:span.text-red-600 err])]])))
