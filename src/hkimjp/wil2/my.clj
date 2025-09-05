(ns hkimjp.wil2.my
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [taoensso.telemere :as t]))

(def sent '[:find ?e ?pt
            :in $ ?login
            :where
            [?e :wil2 "point"]
            [?e :login ?login]
            [?e :pt ?pt]])

(def sent-count '[:find (count ?e)
                  :in $ ?login ?pt
                  :where
                  [?e :wil2 "point"]
                  [?e :login ?login]
                  [?e :pt ?pt]])

; (ds/qq sent "hkimura")
; (ds/qq sent-count "hkimura" 1)
; (ds/qq sent-count "hkimura" 2)
; (ds/qq sent-count "hkimura" -1)

(defn my [request]
  (let [user (user request)]
    (t/log! :info (str "my " user))
    (page
     [:div
      [:div.text-2xl (user  request) "'s points"]
      [:div
       [:div.font-bold "Points by Sendings"]
       [:div.font-bold "Points by Receivings"]]])))
