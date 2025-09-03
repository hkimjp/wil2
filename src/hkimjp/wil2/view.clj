(ns hkimjp.wil2.view
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as response]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def version "0.2.0")

(defn header []
  [:div.flex.bg-green-900.items-baseline.gap-x-8
   [:div.text-2xl.font-medium.text-white "WILL2"]
   [:div.text-xl.font-medium.text-white
    [:span.px-1.hover:bg-red-900 "days"]]
   [:div.text-xl.font-medium.text-white "my"]
   [:div.text-xl.font-medium.text-white "list"]
   [:div.text-xl.font-medium.text-white
    [:form {:method "post" :action "/logout"}
     (h/raw (anti-forgery-field))
     [:button.px-1.text-white.hover:bg-red-900 "logout"]]]
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
     (header)
     [:div content]
     [:div footer]]]])

(defn page
  [content]
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      response/response
      (response/header "Content-Type" "text/html")))
