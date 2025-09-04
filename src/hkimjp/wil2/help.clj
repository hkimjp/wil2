(ns hkimjp.wil2.help
  (:require
   [hkimjp.wil2.view :refer [page]]))

(defn help
  [request]
  (page
   [:div
    [:div.text-2xl.font-medium "Help"]
    [:ul.mx-4.list-disc
     [:li "todays"
      [:ul.mx-4.list-disc
       [:li "今日の WIL 未提出時"
        [:p "upload ボタンを表示する"]]
       [:li "今日の WIL を提出済み"
        [:p "WIL 提出済みのアカウントをリストする。
             アカウントをクリックするとその人の WIL を表示するので
             いいね、まあまあ、悪いねの点数をつける。"]]]]
     [:li "my"
      [:p "自分の WIL 送信ポイント、受信ポイントを表示する。"]]
     [:li "weeks"
      [:p "授業の日付のリスト。クリックするとその日に届いた WIL を表示する。"]]
     [:li "logout" [:p "WIL をログアウトする。"]]
     [:li "HELP" [:p "このページを表示。"]]
     [:li "(admin)" [:p "hkimura 専用。"]]]]))
