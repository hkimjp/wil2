(ns hkimjp.wil2.routes
  (:require
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.telemere :as t]
   [hkimjp.wil2.middleware :as m]
   [hkimjp.wil2.admin :as admin]
   [hkimjp.wil2.help :refer [help]]
   [hkimjp.wil2.login :refer [login login! logout!]]
   [hkimjp.wil2.my :refer [my]]
   [hkimjp.wil2.todays :as todays]
   [hkimjp.wil2.weeks :refer [list-days browse]]))

(defn routes
  []
  [["/"         {:get login :post login!}]
   ["/logout"   logout!]
   ["/help"     {:get help}]
   ["/admin"    {:middleware [m/wrap-admin]}
    [""         {:get admin/admin}]]
   ["/wil2"     {:middleware [m/wrap-users]}
    [""         {:get todays/switch}]
    ["/todays"  {:get todays/todays}]
    ["/upload"  {:get todays/upload :post todays/upload!}]
    ["/md/:eid" {:get todays/md}]
    ["/weeks"   {:get list-days}]
    ["/browse"  {:get browse}]
    ["/my"      {:get my}]]])

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
