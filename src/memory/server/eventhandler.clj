(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.websocket :as websocket]))

(defn multicast-event-to-game [event game-id]
  (let [game (:game-id @games/games)
        player-one ([:player-one :uid] game)
        player-two ([:player-two :uid] game)]
          (doseq [uid (:any [player-one player-two])]
             (websocket/chsk-send! uid event))))

(defn create-game-handler [uid]
    (games/add-new-game uid))

(declare handle-card-selected validate-player-action get-active-player-uid determine-game-state)

(defn handle-card-selected [uid game]
  (validate-player-action uid game))

(comment
(defmulti forward-game-when determine-game-state)
(defmethod forward-game-when :first-card-selected)
(defmethod forward-game-when :cards-matching)
(defmethod forward-game-when :cards-not-matching)
(defmethod forward-game-when :game-finished))

(defn determine-game-state [game]
 (let [{:keys [deck]} game
       unresolved (filter-unresolved-cards deck)
       turned (filter-turned-cards unresolved)]
   (when (= (count turned) (unresolved)) ;two cards are left, both are turned
      :game-finished)
   (when (= 1 (count turned))
      :first-card-selected)
   (when (= (count turned) 2)
      ((if (match? turned)
        :cards-matching
        :cards-not-matching)))))


(defn match? [[card-one card-two]]
  (= (:url card-one) (:url card-two)))

(defn filter-unresolved-cards [deck]
  (filter #(= (:resolved %) 0)) deck)

(defn filter-turned-cards [deck]
  (filter #(true? (:turned %))) deck)

(validate-player-action [sender-uid game]
  (if (not= sender-uid (get-active-player-uid game))
     (throw (.Exception (str "Event received from player "))))
)

(defn get-active-player-uid [game]
  ((:players game)(:active-player game))
)

(defn join-game-handler [uid game-id]
  ;; TODO ADD Error handling
  (def game (games/add-player-to-game uid game-id))
  (multicast-event-to-game [:game/player-joined-game {
                                 :uid uid
                                 :game game } game-id])
  (multicast-event-to-game [:game/player-selected (get-in game [:turn :selected-player])])
  ;; is this the second event necessary? Clients can read, who is activated.
  )
