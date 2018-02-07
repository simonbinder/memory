(ns memory.server.games
  (:require
    [memory.server.game :as game]
    [digest :as digest]
    [clojure.java.io :as io]))
(def users (atom {}))
(def games (atom {}))

;;(require 'digest '[clojure.java.io :as io])

(defn player-nil? [player-key game-id]
  (nil? ([game-id player-key :uid])))

(defn add-player-to-game [uid game-id]
  (if (player-nil? [:player-two game-id])
    (swap! games assoc-in [game-id :player-two :uid] uid)
    (if (player-nil? [:player-one game-id])
       (swap! games assoc-in [game-id :player-one :uid] uid)
       (throw (Exception. "There are already two players participating in this game.")))))

;; too long TODO: not random - same value always generates same id?
(defn create-game-id [uid]
  (digest/md5 uid))

;;does this append the single elements or append the whole map?
(defn add-new-game [uid]
  (let [game-id (create-game-id uid) game (create-new-game uid)]
    (swap! games assoc-in [game-id] game)
    (swap! users assoc-in [uid] game-id)
    game-id
   ))

(defn create-new-game [player-one-uid]
  {
   :player-one {
                :uid player-one-uid
                :resolved-pairs (list)}
   :player-two {
                :uid nil
                :resolved-pairs (list)}
   :closed-cards (create-deck)
   :active-user (rand-int 1)})

(defn load-deck-files[]
  (def directory (clojure.java.io/file  "./resources/public/assets"))
  (def files
    (for [file (file-seq directory)]
    (when (.isFile file)
      (.getPath file))))
  (def files-clean (remove nil? files))
  (println "Files: " files-clean)
  files-clean)

(def deck (load-deck-files))

(defn generate-id[start-value]
   (def ids (take 18 (iterate (partial + 2) start-value)))
   ids)

;; TODO: shuffle
(defn create-deck[]
  (def closed-cards-1 (apply assoc {} (interleave (generate-id 0) deck)))
  (def closed-cards-2 (apply assoc {} (interleave (generate-id 1) deck)))
  (def closed-cards (merge closed-cards-1 closed-cards-2))
  closed-cards)

(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
