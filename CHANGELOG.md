# Unreleased

* make it possible to check other student's points or average
* 変更に強い方法を身につける、編み出さないと。
  - develop/production をキレイに切り分けるにパワーをかけるのは無駄か。
  - production メインで、develop はもっとゴツゴツでもいい。
  - 具体的には、細かく if/when で条件分けすると変更に弱いコードになる。
  - develop でひとまとめをやめ、細かく develop-what みたいなので狙い撃ちにする。
  - コードを短くしようとするとドツボる。
* 30秒以内ルールに抵触はマイナス点とか。

# 0.3.14-SNAPSHOT

- divided todays.clj into upload.clj and rating.clj
- removed todays.clj

# 0.3.13 (2025-10-02)

- **BREAKING** changed/added radis vars rols

    wil2:<user>     list of rating times
    wil2:<user>:eid list of `eids` to which have sent ratings
    wil2:<user>:pt  last answered time

- fixed by the following change: submit/rating で塗りつぶしたのに、weeks の author: で表示している。
- changed: author の表示やめて updated に変えた。
- submit やめてやっぱり upload
- added todays/can-upload?
- changed todays/hxlink - hx-tigger "mouseenter"
- added `:nrepl` alias in `deps.edn`
- docker volume /usr/src/app/storage

# 0.3.12 (2025-09-30)

- bug fixed - true/false がひっくり返っていた。

    uploaded? (seq (ds/qq query (user request) (today)))

# 0.3.11 (2025-09-29)

- docker container - docker 母艦上でファイル .env 中の PORT, AUTH を調整すること。

# 0.3.10 (2025-09-29)

- display authors in the weeks page
- anonymize uploaders

# 0.3.9 (2025-09-29)

- refactored

# 0.3.8 (2025-09-29)

- WIL の評価を送信できる期間を授業当日から3日間に広げた（動作未確認）
- redirect "/wil2/todays" after uploading
- updated libraries

| :file    | :name                         | :current | :latest |
|----------|-------------------------------|----------|---------|
| deps.edn | io.github.hkimjp/carmine-farm | 0.2.4    | 0.2.9   |
|          | org.clojure/clojure           | 1.12.2   | 1.12.3  |
|          | ring/ring-defaults            | 0.6.0    | 0.7.0   |
|          | ring/ring-jetty-adapter       | 1.14.2   | 1.15.3  |
| pom.xml  | org.clojure/clojure           | 1.12.2   | 1.12.3  |


# 0.3.7 (2025-09-27)

- `git flow release` again
- displayed sum of received points
- displayed sum of sent points

# 0.3.6 (2025-09-17)

- no check weekday when DEVELOP=true
- fixed typo in `Justfile`
- refactored

# 0.3.5 (2025-09-16)

- weekday restriction - accept submissions/comments only on tuesday.

# 0.3.4 (2025-09-16)

- redirect tailwindcss outputs to `/dev/null` by Justfile

# 0.3.3 (2025-09-12)

- sort points by date, chronologically(ascending)
- removed slf4j-nop, muuntaja from `deps.edn`
- display `develop` when DEVELOP=true
- renamed `link` to `hx-link`, `button` to `hx-button` in todays.clj
- ranamed Todays, "uploaded"  to "upload (filtered)"

# 0.3.2 (2025-09-10)

- refactor
- weeks.clj - (sort (dates))
- bump-version-local.sh does not make .bak files
- not DEBUG, DEVELOP - (if (env :develop) develop-code production-code)
- log level - :info, :debug, :error, :warn
- added view/html
- added max-count and min-interval
- sort dates
- does not display /wil2/todays flash
- display time under `send-points` section.

# 0.3.1 (2025-09-09)

- bug: the second call wipes out flash.

    2025-09-09T01:29:36.608667Z INFO LOG m24.local hkimjp.wil2.view[50,3] page
    2025-09-09T01:29:36.610139Z INFO LOG m24.local hkimjp.wil2.view[50,3] page

- solved by emulate flash by using REDIS.
- provide REDIS, DATASCRIPT via env var
- when click `upload`, check frequency limit
- when click `vote`, check maximum limit

# 0.3.0 (2025-09-09)

- restricted frequency the good/bad voting par a minite.
- restricted max voting a day to 5.
- wil2 stacks when not connected to redis-server
- update deps - hkimjp/carmine-farm 0.2.4
- span.inline-block - why is this needed?
- redis key:

    (c/lpush (str "wil2:" user ":" (today)) id)
    (c/setex (str "wil2:" user ":pt") 60 id)

- prevent duplicate submissions to one upload by display filtered uploads after sending points.
- renamed `todays/upload` to `todays/upload!`
- renamed `/my` to `/points`

# 0.2.9 (2025-09-08)

- autocomplete="username" and autocomplete="current-password" in login.clj
- alias `:run-m` which is called by `just run`
- changed order calling - datascript first, then jetty (easy to find the port)
- removed `:date` from point data structure
- changed navbar - my -> points
- use `---` (three dashes) for <hr>
- sort `my` by date. reverse?
- added `io.github.hkimjp/carmine-farm` to dependencies.
- added `.env-template`

# 0.2.8 (2025-09-06)

- favicon.ico - copied from WIL1.
- fixed - show upload button if the user has not yet submitted today's markdown.
- checked ds - it works.
- deployed over to tiger.melt.
- improved `my/points for received`

# 0.2.7.1 (2025-09-05)

- fixed: in `my` page, displays other than myself uploads.
  typo `:wil2` as `?wil`.

# 0.2.7 (2025-09-05)

- improved HELP
- points for sending
- aggregation - can see one's sends/receives
- improved `points for received`

# 0.2.6 (2025-09-05)

- title `WIL2`
- changed nabvar order: todays weeks my logout HELP (admin)
- links hover:underline
- new route - post /wil2/point/:pt
- attibute :wil2 identifiying uploads and points, etc.
- short descriptions at the top of each page.
- can send good/bad to the uploads

# 0.2.5 (2025-09-05)

- added h1, h2, h3 definitions into `css/input.css`
- ul {list-style-type: disc}
- ol {list-style-type: decimal}
- show error page if try to upload without selecting a file
- /wil2/weeks - display the dates in which at least an upload found.
  click the date will show the uploads received on that day.
- can see week-by-week uploads

# 0.2.4 (2025-09-04)

- :wil2/type [upload|answer|...]?
- retrieved today's markdown.
- can select/display uploaded expanded markdown

# 0.2.3 (2025-09-04)

- todays/upload - upload markdown files.
    - in form: enctype="multipart/form-data"
    - htmx: :hx-encoding "multipart/form-data"
- can upload markdown files

# 0.2.2 (2025-09-04)

- `git rm --cached resources/public/assets/css/output.css`
- change post logout to get

    [DOM] Found 2 elements with non-unique id `#__anti-forgery-token`

- help page
- added `io.github.nextjournal/markdown` to dependencies.
- build success. WIL.jar=72MB, WIL2.jar=44MB.

    clojure -T:build ci

- ensured `start-or-restore`, `ds/put!`, `ds/qq`, etc.
- can switch `upload` and `todays`
- added `hkimjp.wil2.util`

# 0.2.1 (2025-09-04)

- `tailwindcss --watch=always` is a go.
- added `hkimjp/datascript-storage-javatime` in dependencies.
- navbar menu - todays, my, weeks, logout, help
- added `hkimjp/wil2/my.clj`
- added `hkimjp/wil2/todays.clj`
- added `hkimjp/wil2/weeks.clj`
- changed routes.clj - :as -> :refer [...]

# 0.2.0 (2025-09-03)

- router started to work
- version in view.clj
- bump-version-local.sh
- nav bar(incomplete)
- post logout(need anti-forgery-field)

# 0.1.0 (2025-09-03)

- remote repository - https://github.com/hkimjp/wil2
