(ns hkimjp.wil2.system
  (:require
   [environ.core :refer [env]]
   [ring.adapter.jetty :as jetty]
   [taoensso.telemere :as t]
   [hkimjp.wil2.routes :as routes]
   [hkimjp.datascript :as ds]))

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

(defn start-ds []
  (ds/start-or-restore {:url "jdbc:sqlite:storage/wil2.sqlite"}))

(defn stop-ds []
  (ds/stop))

(defn start-system []
  (start-jetty)
  (start-ds))

(defn stop-system []
  (stop-server)
  (stop-ds))
