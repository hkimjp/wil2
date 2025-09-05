# TODO

- how to clj-reload?
- test
- is my routes/default-handler working?
- todays/todays - follow todays markdown files uploaded
    - choose n files.
    - after mark his/her markdown file, decrement the n
- aggregation - can see one's sends/receives
- nextjournal.markdown Unknown type:

    ':html-block'.{:type :html-block, :content [{:type :text, :text "<hr>\nhkimura"}]}

- restrict the period allowed uploading
- restrict browsing when he/she does not submit his/her today's wil
- rename `login` namespace to `auth`
- prevent duplicate submissions to one upload.
- restrict number of submissions.


# 0.2.7-SNAPSHOT

- improved HELP
- points for sending

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
