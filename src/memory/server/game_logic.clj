(ns memory.server.game-logic)

(declare
    determine-game-state
    filter-turned-cards
    cards-match?
    forward-game-when
    set-turned-cards-as-resolved-by
    reset-turned-cards
    change-active-player
    update-deck-in-game
    game-finished?
    filter-unresolved-cards)


(defn determine-game-state [game]
   (let [turned (-> game :deck filter-turned-cards)]
     (if (= 1 (count turned))
       :first-card-selected
       (if (cards-match? turned)
           :cards-matching
           :cards-not-matching))))

(defn filter-turned-cards [deck]
      (filter #(:turned %) deck))

(defn cards-match? [[card-one card-two]]
      (= (:url card-one) (:url card-two)))



(defmulti forward-game-when determine-game-state)

(defmethod forward-game-when :first-card-selected [client-game]
  (println "first cards selected"))

(defmethod forward-game-when :cards-matching [client-game]
    (let [deck (get client-game :deck)
          active-player (get client-game :active-player)]
        (-> deck
           (set-turned-cards-as-resolved-by active-player)
           (reset-turned-cards)
           (update-deck-in-game client-game))))

(defmethod forward-game-when :cards-not-matching [client-game]
  (let [deck (get client-game :deck)
        active-player (get client-game :active-player)]
      (-> deck
          (reset-turned-cards)
          (update-deck-in-game client-game)
          (change-active-player))))

(defn set-turned-cards-as-resolved-by [deck active-player]
    (doseq [card deck]
        (when (:turned card)
            (assoc-in card [:resolved] active-player))))

(defn reset-turned-cards [deck]
    (doseq [card deck]
        (when (:turned card)
            (assoc-in card [:turned] false))))

(defn update-deck-in-game [deck game]
    (assoc-in game [:deck] deck))

(defn change-active-player [game]
    (let [old-active-player (:active-player game)
          new-active-player (- 3 old-active-player)]
              (assoc-in game [:active-player] new-active-player)))




(defn game-finished? [game]
    (-> game :deck filter-unresolved-cards count (= 0)))

(defn filter-unresolved-cards [deck]
      (filter #(= (:resolved %) 0) deck))


;; --------------------------

;;TODO Do we need this?
(defn validate-player-action [sender-uid game]
  (if (not= sender-uid (get-active-player-uid game))
     (throw (.Exception (str "Event received from player "))))
)


(defn get-active-player-uid [game]
       ((:players game)(:active-player game)))
