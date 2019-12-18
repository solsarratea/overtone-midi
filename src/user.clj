(ns user
  (:require [clojure.tools.namespace.repl :refer :all]
            [clojure.java.io :as io]
            [overtone-midi.parser :refer [read-and-parse-midi]]
            [overtone-midi.transcriber :refer [to-code]]))

(defn -load
  []
  (refresh))

(defn- select-tempo
  [atom]
  (println "Enter number for altering tempo: default is 1")
  (let [val (Float/parseFloat (read-line))]
    (println val (int? val))
    (swap! atom assoc :tempo (if (int? val) val 1))))


(defn- select-name
  [atom]
  (println "Enter midi file name")
  (swap! atom assoc :file-name (read-line)))

(defn- select-bounds
  [atom size]
  (println "There are " size " events. Wanna bound selected files? Y/n " )
  (if-not (= (read-line) "n")
    (do
      (println "From: ")
      (swap! atom assoc :lower-bound (Integer/parseInt (read-line)))
      (println "To: ")
      (swap! atom assoc :upper-bound (Integer/parseInt (read-line))))))


(defn- midis-resources
  []
  (map #(subs % 0 (- (.length %) 4)) (.list (io/file "resources/midis"))))

(defn- list-resources
  []
  (println "List resources? Y/n ")
   (if-not (= (read-line) "n")
      (doall (map println (midis-resources)))))

(defn midi2code
  []
  (list-resources)
  (let [config      (atom {})]
    (select-name config)
    (let [midi-events (read-and-parse-midi (:file-name @config))]
      (select-tempo config)
      (select-bounds config (count midi-events))
      (to-code (assoc @config :midi-events
                      (take (:upper-bound @config)
                            (drop (:lower-bound @config) midi-events)))))))

