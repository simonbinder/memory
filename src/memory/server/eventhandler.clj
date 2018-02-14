(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.websocket :as websocket]))

;;----------- send-methods ------------------------
(defn multicast-event-to-game [event game]
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

(declare card-selected-handler validate-player-action)

(defn filter-unresolved-cards [deck]
      (filter #(= (% :resolved) 0) deck))

(defn filter-turned-cards [deck]
      (filter #(:turned %) deck))

(defn cards-match? [[card-one card-two]]
      (= (:url card-one) (:url card-two)))

(defn filter-active-player[game]
  (def uid (get-active-player-uid game))
  (def players (get game :players))
  (def active-player (first (filter (comp #{uid} players) (keys players))))
  active-player)

  (defn change-active-player[game]
    (def active-player (filter-active-player game))
    (if (= active-player 1)
    2
    1))

(defn get-turned-cards [game]
  (def deck (get game :deck))
  (def turned-cards
  (for [card deck]
    (when (true? (get card :turned))
      card)))
  (def turned-cards-clean (remove nil? turned-cards))
  turned-cards-clean)

(defn determine-game-state [uid game]
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

(defmulti forward-game-when determine-game-state)

(defmethod forward-game-when :first-card-selected [uid game]
  (def game-id (get @games/users uid))
  (def changed-game (get @games/games game-id))
  (println "first cards selected")
  (multicast-event-to-game :game/send-game-data changed-game) changed-game)

(defmethod forward-game-when :cards-matching [uid game]
  (def game-id (get @games/users uid))
  (def turned-cards (get-turned-cards game))
  (def deck (get game :deck))
  (println turned-cards)
  (doseq [card turned-cards]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :resolved] (filter-active-player game))
  )
  (doseq [card deck]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :turned] false)
  )
  (swap! games/games assoc-in [game-id :active-player] (change-active-player game))
  (def changed-game (get @games/games game-id))
  (println "cards matching")
  (multicast-event-to-game :game/send-game-data changed-game) changed-game)

(defmethod forward-game-when :cards-not-matching [uid game]
  (def game-id (get @games/users uid))
  (def deck (get game :deck))
  (doseq [card deck]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :turned] false)
  )
  (def changed-game (get @games/games game-id))
  (println "cards not matching")
  (multicast-event-to-game :game/send-game-data changed-game) changed-game)

(defmethod forward-game-when :game-finished [uid game]
  (def game-id (get @games/users uid))
  (def turned-cards (get-turned-cards game))
  (doseq [card turned-cards]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :resolved] (filter-active-player game))
  )
  (doseq [card (get game :deck)]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :turned] false)
  )
  (def changed-game (get @games/games game-id))
  (multicast-event-to-game :game/game-finished changed-game)
  changed-game
  )

;;TODO Do we need this?
(defn validate-player-action [sender-uid game]
  (if (not= sender-uid (get-active-player-uid game))
     (throw (.Exception (str "Event received from player "))))
)

(defn card-selected-handler [uid game]
  ;; TODO Should we check for valid input from client? (validate-player-action uid game)
  (forward-game-when uid game)
)

(defn create-game-handler [uid]
     (games/add-new-game uid))

(defn join-game-handler [uid game-id]
  (if (nil? (get @games/games game-id))
    ((send-error :error/game-not-found uid "Game does not exist")
    (throw (Exception. "Game does not exist."))))
  (games/add-player-to-game uid game-id)
  (def game (get @games/games game-id))
  (multicast-event-to-game :game/send-game-data game))


;; For testing demo

(defn four [] 4)
(defn match [[card-one card-two]]
   (= (:value card-one) (:value card-two)))
