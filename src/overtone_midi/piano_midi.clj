(ns overtone-midi.piano-midi
  (:require [overtone-midi.parser :as parser]
            [overtone.live      :as live]
            [overtone.inst.piano :refer [piano]]
            [overtone.music.time :refer [now]]))

(defn- live-piano
  "Plays event on piano at specified time. "

  [{:keys [note velocity duration] :as event} time]
  (live/at time
    (piano
          :note note
          :velocity velocity
          :sustain (.floatValue (/ duration 1000) ))))

(defn play-on-piano
  "Plays sequence of events on piano.
   Make it faster/slower by using tempo."

  ([events] (play-on-piano events 1))
  ([events tempo]
  (loop [ time        (now)
         [t0 actuals] (first events)
         ev-rest      (rest events)]
    (when-let [first (first ev-rest)]
      (let [[t1 _ :as next]   first
         in-between-time    (* tempo (- t1 t0))
          next-time           (+ time in-between-time)]
      (when-not (nil? actuals)
        (do
          (doall
            (map #(live-piano % time) actuals))
          (recur next-time next (rest ev-rest)))))
      )
    )))


(comment
  (play-on-piano (parser/read-and-parse-midi "love-dream")))


(play-on-piano (parser/read-and-parse-midi "Tian"  ) 8)

(live/stop)
