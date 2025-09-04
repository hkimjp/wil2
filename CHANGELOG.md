# TODO

- no effect reload/reload. how to use?
- test


# 0.2.2-SNAPSHOT

- `git rm --cached resources/public/assets/css/output.css`
- change post logout to get - `[DOM] Found 2 elements with non-unique id #__anti-forgery-token`
- help page
- added `io.github.nextjournal/markdown` to dependencies.
- build success. WIL.jar=72MB, WIL2.jar=44MB.

```
> clojure -T:build ci
```

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
