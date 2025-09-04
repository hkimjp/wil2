(ns hkimjp.wil2.view
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def version "0.2.0")

(def menu "text-xl font-medium text-white px-1 hover:bg-sky-400")

(defn navbar []
  [:div.flex.bg-green-900.items-baseline.gap-x-8
   [:div.text-2xl.font-medium.text-white "WIL2"]
   [:div.text-xl.font-medium.text-white
    [:span.px-1.hover:bg-sky-400 "days"]]
   [:div {:class menu} "my"]
   [:div {:class menu} "list"]
   [:div.text-xl.font-medium.text-white
    [:form {:method "post" :action "/logout"}
     (h/raw (anti-forgery-field))
     [:button.px-1.text-white.hover:bg-sky-400 "logout"]]]
   [:div.text-xl.font-medium.text-white "HELP"]])

(def footer
  [:div.text-base
   [:hr]
   "hkimura " version])

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
     (navbar)
     [:div content]
     [:div footer]]]])

(defn page
  [content]
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      response/response
      (response/header "Content-Type" "text/html")))
