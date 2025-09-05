(ns hkimjp.wil2.view
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as response]))

(def version "0.2.5-SNAPSHOT")

(def ^:private menu "text-xl font-medium text-white px-1 hover:bg-sky-400")

(defn navbar []
  [:div.flex.bg-green-900.items-baseline.gap-x-4
   [:div.text-2xl.font-medium.text-white "WIL2"]
   [:div {:class menu} [:a {:href "/wil2"} "todays"]]
   [:div {:class menu} [:a {:href "/wil2/my"} "my"]]
   [:div {:class menu} [:a {:href "/wil2/weeks"} "weeks"]]
   [:div {:class menu} [:a {:href "/logout"} "logout"]]
   [:div {:class menu} [:a {:href "/help"} "HELP"]]
   [:div {:class menu} [:a {:href "/admin"} "(admin)"]]])

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
     content
     footer]]])

(defn page
  [content]
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      response/response
      (response/header "Content-Type" "text/html")))
