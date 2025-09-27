(ns hkimjp.wil2.core
  (:gen-class)
  (:require [hkimjp.wil2.system :as system]))

(defn -main [& _args]
  (system/start-system))
