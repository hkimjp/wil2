(ns hkimjp.wil2.admin
  (:require
   [environ.core :refer [env]]
   [hkimjp.carmine :as c]
   [hkimjp.wil2.uploads :refer [max-count min-interval]]
   [hkimjp.wil2.util :refer [user today safe-vec]]
   [hkimjp.wil2.view :refer [page]]))

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

(defn admin
  [request]
  (let [user (user request)]
    (page
     [:div.mx-4
      [:div.text-2xl "Admin"]
      (env-var-section)
      (redis-var-section user)])))

(comment
  (-> (c/lrange (format "wil2:%s:%s" "hkimura" (today)))
      safe-vec)
  (->> (c/lrange "wil2:hkimura:answered")
       (interpose " ")
       pr-str)
  (-> (c/lrange "wil2:hkimura:2025-10-06")
      safe-vec)

  :rcf)

