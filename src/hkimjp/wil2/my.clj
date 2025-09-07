(ns hkimjp.wil2.my
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [nextjournal.markdown :as md]
   [taoensso.telemere :as t]))

(def sent-pt '[:find ?e
               :in $ ?login ?pt
               :where
               [?e :wil2 "point"]
               [?e :login ?login]
               [?e :pt ?pt]])

(defn- ct [user pt]
  (count (ds/qq sent-pt user pt)))

(def recv-pt '[:find ?e ?pt
               :in $ ?login
               :where
               [?e :wil2 "point"]
               [?e :pt ?pt]
               [?e :to/id ?id]
               [?id :login ?login]])

(def my-uploads '[:find ?e ?date ?md
                  :in $ ?login
                  :where
                  [?e :wil2 "upload"]
                  [?e :login ?login]
                  [?e :date ?date]
                  [?e :md ?md]])

(def my-points '[:find ?e ?pt
                 :in $ ?id
                 :where
                 [?e :wil2 "point"]
                 [?e :to/id ?id]
                 [?e :pt ?pt]])

(defn- points [eid]
  [:div.my-2
   [:span.font-bold.text-red-600 "received points: "]
   (str (reduce + (map second (ds/qq my-points eid))))
   [:hr]])

(defn my [request]
  (let [user (user request)
        recv (group-by second (ds/qq recv-pt user))]
    (t/log! :info (str "my " user))
    (page
     [:div.mx-4
      [:div.text-2xl user "'s points"]
      [:div
       [:div.font-bold.py-2 "送信ポイント"]
       [:div.mx-4
        "⬆️ " (ct user 2)
        ", ➡️ " (ct user 1)
        ", ⬇️ " (ct user -1)]
       [:div.font-bold.py-2 "受信ポイント"]
       [:div.mx-4
        "⬆️ " (count (recv 2))
        ", ➡️ " (count (recv 1))
        ", ⬇️ " (count (recv -1))]]
      [:div.font-bold.py-2 "自分 WIL"]
      [:p.mx-4 "それぞれで受け取ったポイントを WIL の下に表示している。"]
      [:div
       (for [[e date md] (sort-by second (ds/qq my-uploads user))]
         (conj (-> md
                   md/parse
                   md/->hiccup)
               (points e)))]])))

(comment
  (ds/qq my-uploads "hkimura")
  :rcf)

