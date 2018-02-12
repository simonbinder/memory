(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.websocket :as websocket]))

;;----------- send-methods ------------------------
(defn multicast-event-to-game [event game-id]
  (def game (get @games/games game-id))
  (def game-uids (vals (get game :players)))
    (doseq [uid game-uids]
       (websocket/chsk-send! uid [event game])))

(defn multicast-event-with-message [event message game-id]
    (def game (get @games/games game-id))
    (def game-uids (vals (get game :players)))
      (doseq [uid game-uids]
      (if-not (nil? uid)
        (websocket/chsk-send! uid [event message]))))

;;------------------- util-methods ------------------------
(defn filter-players [uid game-id]
    (first (filter (comp #{uid}  (get (get @games/games game-id) :players))
      (keys (get (get @games/games game-id) :players)))))

(defn game-nil? [game-id]
  (every? empty? (vals (select-keys
        (get (get @games/games game-id) :players) [1 2]))))

;; ------------- handler ---------------------------------
(defn player-disconnected-handler [uid]
  (def game-id (get @games/users uid))
  (def player-index (filter-players uid game-id))
  (swap! games/games assoc-in [game-id :players player-index] nil)
 (swap! games/users dissoc uid)
 (if (game-nil? game-id)
 (swap! games/games dissoc game-id)
 (multicast-event-with-message :game/waiting-for-player
      "Waiting for second player to connect" game-id)))

(defn send-error [event uid message]
  (websocket/chsk-send! uid [event message]))

(defn get-active-player-uid [game]
       ((:players game)(:active-player game)))

(declare card-selected-handler validate-player-action
  get-active-player-uid determine-game-state)

(defn get-turned-cards [game]
  (def deck (get game :deck))
  (def turned-cards
  (for [card deck]
    (when (true? (get card :turned))
      (get card :id))))
  (def turned-cards-clean (remove nil? turned-cards))
  turned-cards-clean)

(defmulti forward-game-when (fn [x1 x2] ()))

(defmethod forward-game-when :first-card-selected [uid game]
  (def game-id (get @games/users uid))
  (swap! @games/games assoc-in [game-id] game)
  (multicast-event-to-game :game/send-game-data (get @games/users uid)))

(defmethod forward-game-when :cards-matching [uid game]
  (def game-id (get @games/users uid))
  (swap! @games/games assoc-in [game-id] game)
  (def turned-ids (get-turned-cards game))
  (for [turned-id turned-ids]
    ((swap! @games/games assoc-in [game-id :deck :resolved] (get-active-player-uid game))
    (swap! @games/games assoc-in [game-id :deck :turned] false))
  )
  (multicast-event-to-game :game/send-game-data (get @games/users uid)))

(defmethod forward-game-when :cards-not-matching [uid game]
  (def game-id (get @games/users uid))
  (swap! @games/games assoc-in [game-id] game)
  (def turned-ids (get-turned-cards game))
  (for [turned-id turned-ids]
    (swap! @games/games assoc-in [game-id :deck :turned] false)
  )
  (multicast-event-to-game :game/send-game-data (get @games/users uid)))

(defmethod forward-game-when :game-finished [uid game]
  (def game-id (get @games/users uid))
  (swap! @games/games assoc-in [game-id] game)
  (def turned-ids (get-turned-cards game))
  (for [turned-id turned-ids]
    ((swap! @games/games assoc-in [game-id :deck :resolved] (get-active-player-uid game))
    (swap! @games/games assoc-in [game-id :deck :turned] false))
  )
  (multicast-event-to-game :game/game-finished (get @games/users uid))
  )

  (defn cards-match? [[card-one card-two]]
      (= (:url card-one) (:url card-two)))

  (defn filter-unresolved-cards [deck]
        (filter #(= (% :resolved) 0) deck))

  (defn filter-turned-cards [deck]
        (filter #(:turned %) deck))

(defn determine-game-state [game]
 (let [{:keys [deck]} game
       unresolved (filter-unresolved-cards deck)
       turned (filter-turned-cards unresolved)]
   (if (= 1 (count turned))
     :first-card-selected
     (if (= (count turned) 2)
        (if (= 2 (count unresolved))
           :game-finished
           (if (cards-match? turned)
               :cards-matching
               :cards-not-matching))))))

(defn validate-player-action [sender-uid game]
  (if (not= sender-uid (get-active-player-uid game))
     (throw (.Exception (str "Event received from player "))))
)

(defn handle-card-selected [uid game]
  (validate-player-action uid game))

(defn create-game-handler [uid]
     (games/add-new-game uid))

(defn join-game-handler [uid game-id]
  (if (nil? (get @games/games game-id))
    ((send-error :error/game-not-found uid "Game does not exist")
    (throw (Exception. "Game does not exist."))))
  (games/add-player-to-game uid game-id)
  (def game (get @games/games game-id))
  (multicast-event-to-game :game/send-game-data game-id))


;; For testing demo

(defn four [] 4)
(defn match [[card-one card-two]]
   (= (:value card-one) (:value card-two)))
