(ns hkimjp.wil2.util
  (:require [java-time.api :as jt]))

(defn user [request]
  (get-in request [:session :identity]))

(defn today []
  (str (jt/local-date)))
