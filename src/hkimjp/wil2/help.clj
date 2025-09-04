(ns hkimjp.wil2.help
  (:require
   [hkimjp.wil2.view :refer [page]]))

(defn help
  [request]
  (page
   [:div
    [:div.text-2xl.font-medium "Help"]
    [:p "under construction"]
    [:ul
     [:li "todays"]
     [:li "my"]
     [:li "weeks"]
     [:li "logout" [:p "logout."]]
     [:li "HELP" [:p "show this page."]]
     [:li "(admin)" [:p "admin only."]]]]))
