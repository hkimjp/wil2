(ns hkimjp.wil2.points
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [nextjournal.markdown :as md]
   [taoensso.telemere :as t]))

(def my-uploads '[:find ?e ?md
                  :in $ ?login
                  :where
                  [?e :wil2 "upload"]
                  [?e :login ?login]
                  [?e :md ?md]])

(def my-point-sent '[:find ?e ?pt
                     :in $ ?id
                     :where
                     [?e :wil2 "point"]
                     [?e :to/id ?id]
                     [?e :pt ?pt]])

(def my-point-recv '[:find ?e ?pt
                     :in $ ?login
                     :where
                     [?e :wil2 "point"]
                     [?e :pt ?pt]
                     [?e :to/id ?id]
                     [?id :login ?login]])

(defn- ct [user pt]
  (count (ds/qq '[:find ?e
                  :in $ ?login ?pt
                  :where
                  [?e :wil2 "point"]
                  [?e :login ?login]
                  [?e :pt ?pt]]
                user pt)))

(defn- received-points [eid]
  [:div.my-2
   [:span.font-bold.text-red-600 "received points: "]
   (str (reduce + (map second (ds/qq my-point-sent eid))))
   [:hr]])

(defn points [request]
  (let [user (user request)
        recv (group-by second (ds/qq my-point-recv user))]
    (t/log! :info (str "my " user "recv " recv))
    (page
     [:div.mx-4
      [:div.text-2xl user "'s points"]
      [:div
       [:div.font-bold.py-2 "送信ポイント"]
       ;; FIXME: 3 回まわるのはダサい。
       [:div.mx-4
        "⬆️ "   (ct user 2)
        ", ➡️ " (ct user 1)
        ", ⬇️ " (ct user -1)]
       [:div.font-bold.py-2 "受信ポイント"]
       ;; FIXME: 合計点出す？
       [:div.mx-4
        "⬆️ "   (count (recv 2))
        ", ➡️ " (count (recv 1))
        ", ⬇️ " (count (recv -1))]
       [:div.font-bold.py-4 "自分の WIL についたポイントは？"]
       [:div
        (for [[e md] (sort-by second (ds/qq my-uploads user))]
          (conj (-> md
                    md/parse
                    md/->hiccup)
                (received-points e)))]]])))
