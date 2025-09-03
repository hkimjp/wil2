(ns hkimjp.wil2.routes
  (:require
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.telemere :as t]
   ;
   [hkimjp.wil2.admin :as admin]
   [hkimjp.wil2.core :as wil2]
   [hkimjp.wil2.help :refer [help]]
   [hkimjp.wil2.login :refer [login login! logout!]]
   [hkimjp.wil2.middleware :as m]))

(defn routes
  []
  [["/" {:get  {:handler login}
         :post {:handler login!}}]
   ["/logout"  {:post {:handler logout!}}]
   ["/help" {:get {:handler help}}]
   ["/wil2"  {:middleware [m/wrap-users]}
    ["/"       {:get {:handler wil2/index}}]
    ["/todays" {:get {:handler wil2/todays}}]
    ["/upload" {:get  {:handler wil2/upload}
                :post {:handler wil2/upload!}}]
    ["/list" {:get {:handler wil2/list}}]
    ["/browse" {:get {:handler wil2/browse}}]
    ["/my"   {:get {:handler wil2/my}}]]
   ["/admin" {:middleware [m/wrap-admin]}
    ["" {:get {:handler admin/admin}}]]])

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
