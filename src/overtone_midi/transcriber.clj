(ns overtone-midi.transcriber
  (:require [overtone-midi.parser :as parser]
            [overtone.music.time :refer [now]]
            [clojure.java.io :as io]))

(defn- namespace-header
  [name]
  (str "(ns overtone-midi." name
       "\n (:require [overtone.live :as live]
        \n           [overtone.music.time :refer [now]]
        \n           [overtone.inst.piano :refer [piano]])) \n"))


(defn- add-time
  [events tempo]
  (println "tempo" tempo)
  (let [processed-events (atom (seq []))]
    (loop [ time        (now)
           [t0 actuals] (first events)
           ev-rest      (rest events)]
      (when-let [first (first ev-rest)]
        (let [[t1 _ :as next]   first
              in-between-time    (* tempo (- t1 t0))
              next-time          (+ time in-between-time)]
          (when-not (nil? actuals)
            (do
              (swap! processed-events concat (map (fn [event] (assoc event :in-between-time in-between-time) ) actuals))
              (println (count @processed-events))
              (recur next-time next (rest ev-rest))))))
      )
    (vec @processed-events)))

(defn- event2code
  "Transcribe event to code. Default: play event with piano at specified time "

  [{:keys [note velocity duration in-between-time] :as event}]
  (str "  (live/at  @time " "\n"
       "    (piano \n"
       "       :note " note "\n"
       "       :velocity " velocity "\n"
       "       :sustain " (.floatValue (/ duration 1000) ) "))\n"
       "  (swap! time + " in-between-time ")\n"))



(defn to-code
  [{:keys [midi-events tempo file-name]}]

  (with-open [w (io/writer (str "src/overtone_midi/" file-name ".clj") :append true)]
    (.write w (namespace-header file-name))
    (.write w "\n(defn play \n[]  \n ")
    (.write w    " (let [time (atom (now))]  \n")
    (doall (map #(.write w (event2code %))  (add-time midi-events tempo)))
    (.write w "nil))")
    ))
