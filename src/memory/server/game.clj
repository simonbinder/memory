(ns memory.server.game)


(defn create-new-game [player-one-uid]
  {
   :player-one {
                :uid player-one-uid
                :resolved-pairs (list)}
   :player-two {
                :uid nil
                :resolved-pairs (list)}
   :closed-cards (take 36 (iterate inc 0))
   :active-user (rand-int 1)})

(add-player-to-game [uid game-id]
  (if (player-nil? [:player-two game-id])
    (swap! games assoc-in [game-id :player-two :uid] uid)
    (if (player-nil? [:player-one game-id]))
       (swap! games assoc-in [game-id :player-one :uid] uid)
       (throw (Exception. "There are already two players participating in this game."))))

(player-nil? [player-key game-id]
  (nil? ([game-id player-key :uid]))


(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
