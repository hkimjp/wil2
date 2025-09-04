(ns hkimjp.wil2.my
  (:require
   [ring.util.response :as response]
   [hkimjp.wil2.view :refer [page]]))

(defn my [request]
  (page [:div "my"]))

