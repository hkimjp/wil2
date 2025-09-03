(ns hkimjp.wil2.routes
  (:require
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.telemere :as t]
   ;
   [hkimjp.wil2.admin :as admin]
   [hkimjp.wil2.core :as core]
   [hkimjp.wil2.help :refer [help]]
   [hkimjp.wil2.login :refer [login login! logout!]]
   [hkimjp.wil2.middleware :as m]))

(defn routes
  []
  [["/"        {:get  {:handler login}
                :post {:handler login!}}]
   ["/logout"  {:post {:handler logout!}}]
   ["/help"    {:get  {:handler help}}]
   ["/wil2"    {:middleware [m/wrap-users]}
    [""        {:get  {:handler core/index}}]
    ["/todays" {:get  {:handler core/todays}}]
    ["/upload" {:get  {:handler core/upload}
                :post {:handler core/upload!}}]
    ["/list"   {:get  {:handler core/list-days}}]
    ["/browse" {:get  {:handler core/browse}}]
    ["/my"     {:get  {:handler core/my}}]]
   ["/admin"   {:middleware [m/wrap-admin]}
    [""        {:get  {:handler admin/admin}}]]])

(defn root-handler
  [request]
  (t/log! :info (str (:request-method request) " - " (:uri request)))
  (let [handler
        (rr/ring-handler
         (rr/router (routes))
         (rr/routes
          (rr/create-resource-handler {:path "/"})
          (rr/create-default-handler
           {:not-found
            (constantly {:status 404
                         :headers {"Content-Type" "text/html"}
                         :body "not found"})
            :method-not-allowed
            (constantly {:status 405
                         :body "not allowed"})
            :not-acceptable
            (constantly {:status 406
                         :body "not acceptable"})}))
         {:middleware [[wrap-defaults site-defaults]]})]
    (handler request)))
