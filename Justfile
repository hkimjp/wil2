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
  just watch >/dev/null 2>&1
  just nrepl

test:
  clojure -M:dev -m kaocha.runner

run:
  clojure -J--enable-native-access=ALL-UNNAMED -M:run-m

up:
  docker compose up

down:
  docker compose down

update:
  clojure -Tantq outdated :upgrade true :force true

build:
  clojure -T:build ci

deploy: build
  scp target/io.github.hkimjp/wil2-*.jar ${DEST}:wil2/wil.jar
  ssh ${DEST} 'sudo systemctl restart wil'
  ssh ${DEST} 'systemctl status wil'

eq: build
  scp target/io.github.hkimjp/wil2-*.jar eq.local:wil2/wil2.jar
  ssh eq.local 'cd wil2 && docker compose restart'

clean:
  rm -rf target
  fd -I bak --exec rm
