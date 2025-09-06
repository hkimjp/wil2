(ns user
  (:require
   [clj-reload.core :as reload]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.system :refer [start-system stop-system]]))

(t/set-min-level! :debug)

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}})

(defn restart-system
  []
  (stop-system)
  (reload/reload)
  (start-system))

(start-system)

;; (reload/reload)
;; (restart-system)

(ds/qq '[:find ?e ?login
         :in $ ?login
         :where
         [?e :wil2 "upload"]
         [?e :login ?login]
         [?e :date "2025-09-06"]]
       "hkimura")

(ds/qq '[:find ?e
         :in $ ?login
         :where
         [?e :wil2 "upload"]
         [?e :login ?login]]
       "hkimura")

(ds/pl 1)
