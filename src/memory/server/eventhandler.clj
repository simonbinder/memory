(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.event-sender :as event-sender]))

;;----------- send-methods ------------------------
(comment
(defn multicast-event-to-game [event game]
  (let [game-uids (vals (get game :players))]
    (doseq [uid game-uids]
       (websocket/chsk-send! uid [event game]))))

(defn multicast-event-with-message [event message game-id]
    (let [game (get @games/games game-id)
      game-uids (vals (get game :players))]
      (doseq [uid game-uids]
      (if-not (nil? uid)
        (websocket/chsk-send! uid [event message])))))
)
;;------------------- util-methods ------------------------
(defn get-player-for-uid-from-game [uid game-id]
    (first (filter (comp #{uid}  (get (get @games/games game-id) :players))
      (keys (get (games/get-game game-id) :players)))))


(defn no-player-left? [game]
  (every? nil? (-> game :players vals))

;; ------------- handler ---------------------------------
(defn player-disconnected-handler [uid]
    (let [game-id (get @games/users uid)
          player-index (get-player-for-uid-from-game uid game-id)
          game (-> game-id
                   games/get-game
                   (assoc-in [:players player-index] nil))]
          (swap! games/users dissoc uid)
          (if (no-player-left? game)
              (-> game-id games/remove-game)
              (multicast-event-to-participants-of-game [:game/waiting-for-player "Waiting for second player to connect"] game))))

(defn join-game-handler [uid game-id]
  (let [game (-> game-id games/get-game)]
      (if (nil? game)
         (event-sender/send-error-to-player "Game does not exist")
            ;(throw (Exception. "Game does not exist."))))
         (do
             (games/add-player-to-game uid game-id)
             (event-sender/multicast-game-to-participants game)))))




(declare card-selected-handler validate-player-action)

(defn filter-unresolved-cards [deck]
      (filter #(= (% :resolved) 0) deck))

(defn filter-turned-cards [deck]
      (filter #(:turned %) deck))

(defn cards-match? [[card-one card-two]]
      (= (:url card-one) (:url card-two)))

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


(defn get-active-player-uid [game]
       ((:players game)(:active-player game)))

(defn filter-active-player[game]
  (let [uid (get-active-player-uid game)
    players (get game :players)
   active-player (first (filter (comp #{uid} players) (keys players)))]
  active-player))

(defn change-active-player[game]
    (let [active-player (filter-active-player game)]
        (if (= active-player 1)
            2
            1)))

(defn get-turned-cards [game]
  (let [deck (get game :deck)]
  (let [turned-cards
  (for [card deck]
    (when (true? (get card :turned))
      card))]
  (let [turned-cards-clean (remove nil? turned-cards)]
  turned-cards-clean))))



(defmulti forward-game-when determine-game-state)

(defmethod forward-game-when :first-card-selected [uid game]
  (let [game-id (get @games/users uid)
   changed-game (get @games/games game-id)]
  (println "first cards selected")
  (event-sender/multicast-game-to-participants changed-game)))

(defmethod forward-game-when :cards-matching [uid game]
  (let [game-id (get @games/users uid)
    turned-cards (get-turned-cards game)
    deck (get game :deck)]
  (doseq [card turned-cards]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :resolved] (filter-active-player game))
  )
  (doseq [card deck]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :turned] false)
  )
  (swap! games/games assoc-in [game-id :active-player] (change-active-player game))
  (let [changed-game (get @games/games game-id)]
    (println "cards matching")
    (event-sender/multicast-game-to-participants changed-game))))

(defmethod forward-game-when :cards-not-matching [uid game]
  (let [game-id (get @games/users uid)
    deck (get game :deck)]
  (doseq [card deck]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :turned] false)
  )
  (let [changed-game (get @games/games game-id)]
  (println "cards not matching")
  (event-sender/multicast-game-to-participants changed-game))))

(defmethod forward-game-when :game-finished [uid game]
  (let [game-id (get @games/users uid)
   turned-cards (get-turned-cards game)]
  (doseq [card turned-cards]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :resolved] (filter-active-player game))
  )
  (doseq [card (get game :deck)]
    (swap! games/games assoc-in [game-id :deck (.indexOf (get game :deck) card) :turned] false)
  )
  (let [changed-game (get @games/games game-id)]
  (event-sender/multicast-game-to-participants changed-game))))

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




;; For testing demo

(defn four [] 4)
(defn match [[card-one card-two]]
   (= (:value card-one) (:value card-two)))
