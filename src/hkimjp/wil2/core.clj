(ns hkimjp.wil2.core
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [hkimjp.wil2.view :refer [page]]))

(defn index [request]
  (page
   [:div "index"]))

