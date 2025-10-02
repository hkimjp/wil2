(ns user
  (:require
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   ; [clojure.java.io :as io]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [today]]
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

;; (reload/reload
;; (restart-system)

(defn dummy [user n]
  (doseq [n (range 10)]
    (ds/put! {:wil2 "upload"
              :login user
              :md (str "# dummy\n" n)
              :date (today)
              :updated (jt/local-date-time)})))

(comment
  (def x "this is a document about x." 3)
  (def ^{:doc "which is easy to read?"} y 4)
  x
  y

  (env :develop)
  (if-not (env :develop)
    "not develop"
    "develop")
  (if (env :develop)
    "develop"
    "not develop")
  (= 1 1)

  (env :home)
  (ds/qq '[:find ?e
           :where
           [?e _ _]])
  (c/get (str "wil2:" "hkimura" ":pt"))
  (some? (c/get (str "wil2:" "hkimura" ":pt")))
  (c/ping)
  (c/set "x" 3)
  (c/get "x")
  (c/setex "z" 20 4)
  (c/get "z")

  (today)
  (str (jt/local-date))

  (dummy "hkimura" 10)

  (some #(= "5"  %) ["1" "2" "3" "5"])
  (count (c/lrange "wil2:hkimura:2025-09-08"))
  :rcf)
