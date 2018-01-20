(ns memory.server.game)
(require 'digest)

(defn createNewGame [game-id players]
  {
   :id (create-game-id)
   :player-one {
                :player (:player-one players)
                :resolved-pairs (list)}
   :player-two {
                :player (:player-two players)
                :resolved-pairs (list)}
   :closed-cards (take 36 (iterate inc 0))
   :active-user (rand-int 1)})

(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn create-game-id []
  (digest/md5
    repeatedly 8 #(rand-int 1000)))
