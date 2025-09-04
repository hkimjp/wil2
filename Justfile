set dotenv-load

help:
  just --list

CSS := "resources/public/assets/css"

watch:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --watch=always

minify:
  tailwindcss -i {{CSS}}/input.css -o {{CSS}}/output.css --minify

plus:
  clj -X:dev:plus

nrepl:
  clj -M:dev:nrepl

dev:
  just watch &
  just nrepl

test:
  clojure -M:dev -m kaocha.runner

container-nrepl:
  clj -M:dev -m nrepl.cmdline -b 0.0.0.0 -p 5555

run:
  clojure -M:run-m

update:
  clojure -Tantq outdated :upgrade true :force true

clean:
  rm -rf target
  fd -I bak --exec rm
