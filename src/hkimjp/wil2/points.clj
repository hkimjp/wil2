(ns hkimjp.wil2.points
  (:require
   [hkimjp.datascript :as ds]
   [hkimjp.wil2.util :refer [user]]
   [hkimjp.wil2.view :refer [page]]
   [nextjournal.markdown :as md]
   [taoensso.telemere :as t]))

(def my-uploads '[:find ?e ?date ?md
                  :in $ ?login
                  :where
                  [?e :wil2 "upload"]
                  [?e :date ?date]
                  [?e :login ?login]
                  [?e :md ?md]])

(comment
  (first (ds/qq my-uploads "hkimura"))
  (ds/pl 148))

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
      [:p "送信ポイント、受信ポイントの和が平常点。"]
      [:div
       [:div.font-bold.py-2 "送信ポイント"]
       [:div.mx-4
        (let [a (ct user 2)
              b (ct user 1)
              c (ct user -1)]
          (format "⬆️ %d,  ➡️ %d, ⬇️ %d == %d" a b c (+ a b c)))]
       [:div.font-bold.py-2 "受信ポイント"]
       [:div.mx-4
        (let [a (count (recv 2))
              b (count (recv 1))
              c (count (recv -1))]
          (format "⬆️ %d,  ➡️ %d, ⬇️ %d == %d" a b c (+ (* 2 a) b (* -1 c))))]
       [:div.font-bold.py-4 "自分の WIL と獲得ポイント"]
       [:p "自分が提出した WIL の下に"
        [:span.text-red-600 "獲得ポイント"]
        "を表示している。"]
       [:div
        (for [[e _ md] (sort-by second (ds/qq my-uploads user))]
          (conj (-> md
                    md/parse
                    md/->hiccup)
                (received-points e)))]]])))
