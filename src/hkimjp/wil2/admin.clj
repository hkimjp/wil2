(ns hkimjp.wil2.admin
  (:require
   [environ.core :refer [env]]
   [hkimjp.wil2.uploads :refer [max-count min-interval]]
   [hkimjp.wil2.view :refer [page]]))

(defn admin
  [_request]
  (page
   [:div.m-4
    [:div.text-2xl "admin"]
    [:div.m-4
     [:table
      [:tr [:td.font-bold "develop"] [:td] [:td (env :develop)]]
      [:tr [:td.font-bold "auth"] [:td] [:td (env :auth)]]
      [:tr [:td.font-bold "datascript"] [:td] [:td (env :datascript)]]
      [:tr [:td.font-bold "redis"] [:td] [:td (env :redis)]]
      [:tr [:td.font-bold "max-count"] [:td] [:td max-count]]
      [:tr [:td.font-bold "min-interval"] [:td.w-4 ""] [:td min-interval]]]]]))
