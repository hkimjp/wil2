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

;; (defn abbrev
;;   "shorten string s for concise log."
;;   ([s] (abbrev s 80))
;;   ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
;;            (str/replace-first s pat "$1..."))))

(defn safe-vec
  [v]
  (if (seq v)
    (pr-str (interpose " " v))
    "NIL"))



