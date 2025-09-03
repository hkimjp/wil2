(ns hkimjp.wil2.middleware
  (:require
   [environ.core :refer [env]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]))

(defn- user [request]
  (get-in request [:session :identity]))

(defn wrap-users
  [handler]
  (fn [request]
    (let [user (user request)]
      (t/log! :debug (str "wrap-users " user))
      (if (some? user)
        (handler request)
        (-> (resp/redirect "/")
            (assoc :session {} :flash "need login"))))))

(defn wrap-admin [handler]
  (fn [request]
    (let [user (user request)]
      (t/log! :debug (str "wrap-admin " user))
      (if (= (env :admin) user)
        (handler request)
        (-> (resp/redirect "/")
            (assoc :session {} :flash "admin only"))))))

