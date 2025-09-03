(ns hkimjp.wil2.view
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as response]))

(def header
  [:div.text-base "WIL2"])

(def footer
  [:div.text-base
   [:hr]
   "hkimura"])

(defn- base
  [content]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:link {:type "text/css"
            :rel  "stylesheet"
            :href "/assets/css/output.css"}]
    [:link {:rel "icon"
            :href "/favicon.ico"}]
    [:script {:type "text/javascript"
              :src  "/assets/js/htmx.min.js"
              :defer true}]
    [:title "app"]]
   [:body {:hx-boost "true"}
    [:div
     [:div header]
     [:div content]
     [:div footer]]]])

(defn page
  [content]
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      response/response
      (response/header "Content-Type" "text/html")))
