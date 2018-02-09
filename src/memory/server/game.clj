(ns memory.server.game)
(use 'clojure.walk)
(require 'digest '[clojure.java.io :as io])

; (def deck '("../../assets/pinkfloyd.png" "../../assets/andy-warhol-banana.png" "../../assets/the-xx.jpg" "../../assets/default_cover"))
(defn load-deck-files[]
  (def directory (clojure.java.io/file  "./resources/public/assets"))
  (def files
    (for [file (file-seq directory)]
    (when (.isFile file)
      (.getPath file))))
  (def files-clean (remove nil? files))
  (def deck-list
    (for [file files-clean]
      {:url file
        :turned false
        :resolved 0}))
  (def deck-vector
    (into [] files-map))
  deck-vector)

(defn create-deck[]
  (def deck (into [] (concat (load-deck-files) (load-deck-files))))
  (def deck-shuffled (shuffle deck))
  deck-shuffled)

(defn create-new-game [player-one-uid]
  {
   :players {1 player-one-uid 2 nil}
   :active-player player-one-uid
   :deck (create-deck)})

(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
