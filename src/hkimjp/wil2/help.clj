(ns hkimjp.wil2.help
  (:require
   [hkimjp.wil2.view :refer [page]]))

(defn help
  [_request]
  (page
   [:div
    [:div.text-2xl.font-medium "Help"]
    [:div.m-4
     [:p.py-4 "今日の授業でなにやった？じっと思い出してみ。目を瞑って。"
      "思い出せないような受講の態度は最悪。"
      "記憶にもないことをコピペで取り繕っても最悪は変わりない。"
      "わかったフリ、やったフリするのやめよう。"]
     [:div [:span.font-bold "todays"]
      [:ul.mx-8.list-disc
       [:li "今日の WIL 未提出時"
        [:p "upload ボタンを表示する。マークダウンファイルを選んで送信。"]]
       [:li "今日の WIL を提出済み"
        [:p "WIL 提出済みのアカウントをリストする。"
         "アカウントをクリックするとその人の WIL を表示する。"
         "読んで点数をつける。"]]]]
     [:div [:span.font-bold "weeks"]
      [:p.mx-4 "授業の日付のリスト。クリックするとその日に届いた WIL を表示。"]]
     [:div [:span.font-bold "my"]
      [:p.mx-4 "自分の WIL 送信ポイント、受信ポイントを表示。"]]

     [:div [:span.font-bold "logout"]
      [:p.mx-4 "WIL をログアウト。"]]
     [:div [:span.font-bold "HELP"]
      [:p.mx-4 "このページを表示。"]]
     [:div [:span.font-bold "(admin)"]
      [:p.mx-4 "hkimura 専用。"]]]]))


