(ns memory.server.game)
(use 'clojure.walk)
(require 'digest '[clojure.java.io :as io])

; (def deck '("../../assets/pinkfloyd.png" "../../assets/andy-warhol-banana.png" "../../assets/the-xx.jpg" "../../assets/default_cover"))
(defn load-deck-files[]
  (def directory (clojure.java.io/file  "./resources/public/assets"))
  (def files
    (for [file (file-seq directory)] (.getPath file)))
  (println "Files: " files)
  files)

(def deck (load-deck-files))

;; too long TODO: not random - same value always generates same id?
(defn create-game-id [uid]
  (digest/md5 uid))

(defn generate-id[]
   (def ids (take 36 (iterate inc 0)))
   ids)

(defn create-deck[]
  (def closed-cards (apply assoc {} (interleave (generate-id) deck)))
  closed-cards)

(defn create-new-game [player-one-uid]
  {
   :id (create-game-id player-one-uid)
   :player-one {
                :player player-one-uid
                :resolved-pairs (list)}
   :player-two {
                :player nil
                :resolved-pairs (list)}
   :closed-cards (create-deck)
   :active-user (rand-int 1)})

(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
