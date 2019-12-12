(ns overtone-midi.parser
  (:require [overtone.midi.file :as midi]))


(defn- get-channels
  [{:keys [tracks]}]
  "Return channels of midi"

  (let [all-events (vec (map :events tracks))]
    (apply concat
     (map
        #(let [groups (seq (group-by :channel %))]
            (remove nil? (mapv first groups)))
        all-events))))

(defn midi
  "Reads MIDI from file-path"

  [name]
  (let [midi-obj (midi/midi-file (str "resources/midis/" name ".mid"))]
  (assoc midi-obj :channels (get-channels midi))))

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
                        (select-keys [:note :velocity :timestamp :channel])
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

 (defn parse-track
   "Returns sequence of parsed midi-events"

  [midi-track]
  (let [midi-events   (-> midi-track :events)
        parsed-events (select-and-parse-events (group-by :command midi-events))]
    parsed-events))

(defn parse-single-track
  "Returns sequence of parsed midi-events grouped by timestamp for single "

  [midi]
    (compases parse-track midi))

 (defn parse-midi
   "Returns a single sequence of parsed events grouped by timestamp.
    All tracks/channels are grouped."

  [midi]
   (let [midi-tracks   (-> midi :tracks)
         parsed-tracks (vec (map parse-track midi-tracks))]
     (compases (apply concat parsed-tracks))))

  (defn read-and-parse-midi
    "Composes 'parse-midi' with 'midi' (file reader) functions"

    [midi-name]
    (parse-midi (midi midi-name)))
