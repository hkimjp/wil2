(ns hkimjp.wil2.admin
  (:require
   [hkimjp.wil2.view :refer [page]]))

(defn admin
  [_request]
  (page
   [:div "admin"]))
