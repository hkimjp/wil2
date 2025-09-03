(ns hkimjp.wil2.login
  (:require
   [buddy.hashers :as hashers]
   [environ.core :refer [env]]
   [hato.client :as hc]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   ;;
   [hkimjp.wil2.view :refer [page]]))

(def l22 (or (env :auth) "https://l22.melt.kyutech.ac.jp"))

(defn login
  [request]
  (page
   [:div.mx-4
    [:div.font-bold.p-2 "LOGIN"]
    (when-let [flash (:flash request)]
      [:div {:class "text-red-500"} flash])
    [:div.p-1
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:input.border-1.border-solid.px-1 {:name "login" :placeholder "your account"}]
      [:span.mx-1 ""]
      [:input.border-1.border-solid.px-1 {:name "password" :placeholder "password" :type "password"}]
      [:button.mx-1.px-1.text-white.bg-sky-500.hover:bg-sky-700.active:bg-red-500.rounded-xl "LOGIN"]]]
    [:br]]))

(defn login!
  [{{:keys [login password]} :params}]
  (if (env :no-login)
    (do
      ;; always login success in development
      (t/log! :info (str "login success: " login))
      (-> (resp/redirect "/wil2")
          (assoc-in [:session :identity] login)))
    (try
      (let [resp (hc/get (str l22 "/api/user/" login) {:timeout 3000 :as :json})]
        (if (and (some? resp)
                 (hashers/check password (get-in resp [:body :password])))
          (do
            (t/log! :info (str "login success: " login))
            (-> (resp/redirect "/wil2")
                (assoc-in [:session :identity] login)))
          (do
            (t/log! :info (str "login failed: " login))
            (-> (resp/redirect "/")
                (assoc :session {} :flash "login failed")))))
      (catch Exception e
        (t/log! :warn (.getMessage e))
        (-> (resp/redirect "/")
            (assoc :session {} :flash "enter login/password"))))))

(defn logout!
  [request]
  (t/log! :info (str "logout! " (get-in request [:session :identity])))
  (-> (resp/redirect "/")
      (assoc :session {})))
