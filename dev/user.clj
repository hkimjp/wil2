(ns user
  (:require
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   [taoensso.telemere :as t]
   [hkimjp.wil2.system :refer [start-system stop-system]]))

(t/set-min-level! :debug)

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})

; (reload/reload)

(defn restart-system
  []
  (stop-system)
  (reload/reload)
  (start-system))

(restart-system)
