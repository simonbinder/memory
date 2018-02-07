(comment
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



(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
)
