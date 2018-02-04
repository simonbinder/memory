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



(defn get-sibling-of-card [id]
 (if (odd?)
  (dec id)
  (inc id)))

(defn match? [card-one card-two]
 (= card-two (get-sibling-of-card card-one)))
