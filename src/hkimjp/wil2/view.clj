(ns hkimjp.wil2.view
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]))

(def version "0.3.4")

(def ^:private menu "text-xl font-medium text-white px-1 hover:bg-green-700")

(defn navbar []
  [:div.flex.bg-green-900.items-baseline.gap-x-4
   [:div.text-2xl.font-medium.text-white "WIL2"]
   [:div {:class menu} [:a {:href "/wil2"} "todays"]]
   [:div {:class menu} [:a {:href "/wil2/weeks"} "weeks"]]
   [:div {:class menu} [:a {:href "/wil2/points"} "points"]]
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
    [:title "WIL2"]]
   [:body#body {:hx-boost "true"}
    [:div
     (navbar)
     content
     footer]]])

(defn page
  [content]
  (t/log! :info (str "page"))
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      resp/response
      (resp/header "Content-Type" "text/html")))

;; htmx requires html response.
;; appropriate in this namespace?
(defn html [content]
  (-> (str (h/html content))
      resp/response
      (resp/content-type "text/html")))
