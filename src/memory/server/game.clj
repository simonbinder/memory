(ns memory.server.game)
(require 'digest)

;; too long TODO: not random - same value always generates same id?
(defn create-game-id [uid]
  (digest/md5 uid))

(defn create-new-game [player-one-uid]
  {
   :id (create-game-id player-one-uid)
   :player-one {
                :player player-one-uid
                :resolved-pairs (list)}
   :player-two {
                :player nil
                :resolved-pairs (list)}
   :closed-cards (take 36 (iterate inc 0))
   :active-user (rand-int 1)})

(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
