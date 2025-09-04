(ns hkimjp.wil2.help
  (:require
   [hkimjp.wil2.view :refer [page]]))

(defn help
  [request]
  (page
   [:div
    [:div.text-2xl.font-medium "Help"]
    [:div [:span.font-bold "todays"]
     [:ul.mx-4.list-disc
      [:li "今日の WIL 未提出時"
       [:p "upload ボタンを表示する"]]
      [:li "今日の WIL を提出済み"
       [:p "WIL 提出済みのアカウントをリストする。
             アカウントをクリックするとその人の WIL を表示するので
             いいね、まあまあ、悪いねの点数をつける。"]]]]
    [:div [:span.font-bold "my"]
     [:p.mx-4 "自分の WIL 送信ポイント、受信ポイントを表示する。"]]
    [:div [:span.font-bold "weeks"]
     [:p.mx-4 "授業の日付のリスト。クリックするとその日に届いた WIL を表示する。"]]
    [:div [:span.font-bold "logout"]
     [:p.mx-4 "WIL をログアウトする。"]]
    [:div [:span.font-bold "HELP"]
     [:p.mx-4 "このページを表示。"]]
    [:div [:span.font-bold "(admin)"]
     [:p.mx-4 "hkimura 専用。"]]]))


