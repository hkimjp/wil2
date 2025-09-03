(ns hkimjp.wil2.system
  (:require
   [environ.core :refer [env]]
   [taoensso.telemere :as t]
   [ring.adapter.jetty :as jetty]
   ;;
   [hkimjp.wil2.routes :as routes]))

(defonce server (atom nil))

(defn start-jetty
  []
  (let [port (parse-long (or (env :port) "3000"))
        handler (if (= (env :develop) "true")
                  #'routes/root-handler
                  routes/root-handler)]
    (reset! server (jetty/run-jetty handler {:port port :join? false}))
    (t/log! :info (str "server started at port " port))))

(defn stop-server []
  (when @server
    (.stop @server)
    (t/log! :info "server stopped.")))

(defn start-system []
  (start-jetty))

(defn stop-system []
  (stop-server))
