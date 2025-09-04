(ns hkimjp.wil2.util)

(defn user [request]
  (get-in request [:session :identity]))
