(ns user
  (:require [clojure.tools.namespace.repl :as tools]))

(defn load 
  []
  (tools/refresh))

