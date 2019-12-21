(ns user
  (:require [clojure.tools.namespace.repl :refer :all]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.repl :refer [dir-fn]]
            [overtone-midi.utils :as utils]
            [overtone-midi.parser :refer [read-and-parse-midi]]
            [overtone-midi.transcriber :refer [to-code]]))

(defn -load
  []
  (refresh))

(defn- select-tempo
  [atom]
  (println "Enter number for altering tempo: default is 1")
  (let [val (edn/read-string (read-line))
        tempo (if (number? val) val 1)]
    (swap! atom assoc :tempo tempo)
    (println tempo)))


(defn- select-name
  [atom]
  (println "Enter midi file name")
  (let [name (read-line)]
    (swap! atom assoc :file-name name)
    (println name)))

(defn- select-bounds
  [atom size]
  (println "There are " size " events. Wanna bound selected files? Y/n " )
  (if-not (= (read-line) "n")
    (do
      (println "From: ")
      (let [low (Integer/parseInt (read-line))]
        (swap! atom assoc :lower-bound low )
        (println low))
      (println "To: ")
      (let [up (Integer/parseInt (read-line))]
        (swap! atom assoc :upper-bound up)
        (println up)))))


(defn- midis-resources
  []
  (map #(subs % 0 (- (.length %) 4)) (.list (io/file "resources/midis"))))

(defn- list-resources
  []
  (println "List resources? Y/n ")
   (if-not (= (read-line) "n")
     (doall (map println (midis-resources)))))

(defn- test-transcription
  [{:keys [file-name]} ]
  (println "Play sample? Y/n")
  (if-not (= (read-line) "n")
    (do
      (refresh)
      (let [namespace (str "overtone-midi." file-name)
            number (dec (count (dir-fn (symbol namespace))))]
        (println namespace number)
        (utils/call (str namespace "/sample-" number))))))



(defn- set-configuration
  [config midi-events]
  (select-tempo config)
  (select-bounds config (count midi-events))
  (to-code (assoc @config :midi-events
                      (take (:upper-bound @config)
                            (drop (:lower-bound @config) midi-events))))
  (test-transcription @config))

(defn edit-sample
  [config midi-events]
  (println "Wanna edit sample? Y/n")
  (if-not (= (read-line) "n")
    (do
      (let [namespace (str "overtone-midi." (:file-name @config))
            number (dec (count (dir-fn (symbol namespace))))]
        (ns-unmap (symbol namespace) (symbol (str "sample-" number)))
        (set-configuration config midi-events)
        (edit-sample config midi-events)) )))

(defn midi2code
  []
  (list-resources)
  (let [config      (atom {})]
    (select-name config)
    (let [midi-events (read-and-parse-midi (:file-name @config))]
      (set-configuration config midi-events)
      (edit-sample config midi-events))))
