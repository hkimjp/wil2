(ns hkimjp.wil2.core
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [hkimjp.wil2.view :refer [page]]))

(defn index [request]
  (page
   [:div "index"]))

(defn todays [request]
  (page
   [:div "todays"]))

(defn upload [request]
  (page
   [:div "upload"]))

(defn upload! [request]
  (page
   [:div "upload!"]))

(defn list-days [request]
  (page
   [:div "list"]))

(defn browse [request]
  (page
   [:div "browse"]))

(defn my [request]
  (page [:div "my"]))

