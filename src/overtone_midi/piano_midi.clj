(ns overtone-midi.piano-midi
  (:require [overtone.midi.file :as midi]
            [overtone.live      :as live]
            [overtone.inst.piano :refer [piano]]
            [overtone.music.time :refer [apply-by now]]))

(defn midi
  "Reads MIDI from file-path"

  [name]
  (midi/midi-file (str "resources/midis/" name ".mid")))

(defn- find-note-off
  "Returns true if midi-event is the note-off of input note"

  [original-note {:keys [note command]}]
  (every?  true? [(= note original-note) (= command :note-off)]))

(defn- parse-event
  "Select from midi-event note, velocity and timestamp.
   Add duration between note-on and note-off events."

  [{:keys [note timestamp] :as event} events]
  (let [event-off (first (filter #(find-note-off note %) events))]
    (when (not (nil? event-off))
        (let [duration  (- (:timestamp event-off) timestamp)]
        {:event     (-> event
                        (select-keys [:note :velocity :timestamp])
                        (assoc :duration duration))
        :note-off (vec (remove #(= event-off %) events))}))))

(defn- select-and-parse-events
  "Create only sequence of parsed midi-event from note-on and note-off events."

  [{:keys [note-on note-off]}]
  (let [filtered-note-on (vec (remove #(zero? (:velocity %)) note-on))]
    (second
     (reduce
     (fn [[note-off-events acum] event]
       (let [{:keys [event note-off] :as data} (parse-event event note-off-events)]
         (if (nil? data)
           [note-off-events  acum]
           [note-off         (vec (conj acum event))]
           )))
     [note-off []]
     filtered-note-on))))

(defn- compases
 "Group events according to their timestamp."

 [events]
 (->> events
      (group-by :timestamp )
      (sort-by first)))


(defn parse-midi
  "Returns sequence of parsed midi-events grouped by timestamp.
   It assumes midi has only channel."

 [midi]
 (let [midi-events   (-> midi :tracks first :events)
       parsed-events (select-and-parse-events (group-by :command midi-events))]
   (compases parsed-events)))


(defn- live-piano
  "Plays event on piano at specified time. "

  [{:keys [note velocity duration] :as event} time]
  (println event)
  (println "Plays " note " whith velocity " velocity " for "  (/ duration 1000 ) )
  (live/at time
    (piano
          :note note
          :velocity velocity
          :sustain (.floatValue (/ duration 1000) ))))

(defn play-on-piano
  "Plays sequence of events on piano."

  ([events] (play-on-piano events 1))
  ([events tempo]
  (loop [ time        (now)
         [t0 actuals] (first events)
         ev-rest         (rest events)]
    (let [[t1 _ :as next]   (first ev-rest)
         in-between-time    (* tempo (- t1 t0))
          next-time           (+ time in-between-time)]
      (when (not (nil? actuals))
        (do
          (println "In between time " in-between-time)
          (doall
            (map #(live-piano % time) actuals))
          (recur next-time next (rest ev-rest))))))))


(comment
  (def midif (midi "love-dream"))
  (play-on-piano (take 300 (parse-midi midif))))
