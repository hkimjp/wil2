(ns hkimjp.wil2.admin
  (:require
   [environ.core :refer [env]]
   [hkimjp.wil2.todays :refer [max-count min-interval]]
   [hkimjp.wil2.view :refer [page]]))

(defn admin
  [_request]
  (page
   [:div
    [:div.text-2xl "admin"]
    [:div.m-4
     ; [:div.flex.gap-x-4
     ;  [:div.font-bold "develop"] [:div (env :develop)]]
     ; [:div.flex.gap-x-4
     ;  [:div.font-bold "datascript"] [:div (env :datascript)]]
     ; [:div.flex.gap-x-4
     ;  [:div.font-bold "max-count"] [:div max-count]]
     ; [:div.flex.gap-x-4
     ;  [:div.font-bold "min-interval"] [:div min-interval]]
     ; [:div.flex.gap-x-4
     ;  [:div.font-bold "redis"] [:td] [:div (env :redis)]]
     [:table
      [:tr [:td.font-bold "develop"] [:td (env :develop)]]
      [:tr [:td.font-bold "auth"] [:td (env :auth)]]
      [:tr [:td.font-bold "datascript"] [:td (env :datascript)]]
      [:tr [:td.font-bold "redis"] [:td (env :redis)]]
      [:tr [:td.font-bold "max-count"] [:td max-count]]
      [:tr [:td.font-bold "min-interval　"] [:td min-interval]]]]]))

