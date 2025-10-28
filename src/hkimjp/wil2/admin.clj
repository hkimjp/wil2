(ns hkimjp.wil2.admin
  (:require
   [environ.core :refer [env]]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.uploads :refer [max-count min-interval]]
   [hkimjp.wil2.util :refer [user today safe-vec]]
   [hkimjp.wil2.view :refer [page html]]))

(defn- env-var-section []
  [:div.m-4
   [:div.text-2xl "Env Vars"]
   [:table
    [:tr [:td.font-bold "develop"] [:td "　"] [:td (env :develop)]]
    [:tr [:td.font-bold "auth"] [:td] [:td (env :auth)]]
    [:tr [:td.font-bold "datascript"] [:td] [:td (env :datascript)]]
    [:tr [:td.font-bold "redis"] [:td] [:td (env :redis)]]
    [:tr [:td.font-bold "max-count"] [:td] [:td max-count]]
    [:tr [:td.font-bold "min-interval"] [:td] [:td min-interval]]]])

(defn- redis-var-section [user]
  [:div.m-4
   [:div.text-2xl "Redis Vars"]
   [:div.flex.gap-x-4
    [:div.font-bold (format "wil2:<user>")]
    [:div (or (c/get (format "wil2:%s" user)) "nil")]]
   [:div.flex.gap-x-4
    [:div.font-bold "wil2:<user>:error"]
    [:div (or (c/get (format "wil2:%s:error" user)) "nil")]]
   [:div.flex.gap-x-4
    [:div.font-bold "wil2:<user>:answered"]
    [:div (-> (c/lrange (format "wil2:%s:answered" user)) safe-vec)]]
   [:div.flex.gap-x-4
    [:div.font-bold "wil2:<user>:<today>"]
    [:div (-> (c/lrange (format "wil2:%s:%s" user (today)))
              safe-vec)]]])

(defn- delete-section []
  [:div.m-4
   [:div.text-2xl "Delete2"]
   [:form {:method "post"}
    (h/raw (anti-forgery-field))
    [:input.border-1.rounded-md {:name "eid"}]
    [:button.text-white.px-1.rounded-md.bg-sky-700.hover:bg-red-700.active:bg-red-900
     {:hx-post "/admin/delete"
      :hx-target "#delete"
      :hx-swap "innerHTML"}
     "delete"]]
   [:div#delete "?"]])

(defn delete
  [{{:keys [eid]} :params}]
  (t/log! {:level :info :data {:eid eid}})
  (ds/put! {:db/id (parse-long eid) :wil2 "delete"})
  (html
   [:div (str "eid:" eid " deleted")]))

(defn admin
  [request]
  (let [user (user request)]
    (page
     [:div.mx-4
      [:div.text-2xl "Admin"]
      (env-var-section)
      (redis-var-section user)
      (delete-section)])))

(comment
  (:wil2 (ds/pl 1220))
  (-> (c/lrange (format "wil2:%s:%s" "hkimura" (today)))
      safe-vec)
  (->> (c/lrange "wil2:hkimura:answered")
       (interpose " ")
       pr-str)
  (-> (c/lrange "wil2:hkimura:2025-10-06")
      safe-vec)

  :rcf)

