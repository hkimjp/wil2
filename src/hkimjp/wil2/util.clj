(ns hkimjp.wil2.util
  (:require
   ; [clojure.string :as str]
   [java-time.api :as jt]))

(defn user [request]
  (get-in request [:session :identity]))

(defn today []
  (str (jt/local-date)))

(defn local-time []
  (jt/format "HH:mm:ss" (jt/local-time)))

(defn safe-vec
  [v]
  (if (seq v)
    (pr-str (interpose " " v))
    "NIL"))

(defn- last-tuesday-aux [day]
  (if (jt/tuesday? day)
    (str day)
    (last-tuesday-aux (jt/minus day (jt/days 1)))))

(defn last-tuesday []
  (last-tuesday-aux (jt/local-date)))

; (last-tuesday)
