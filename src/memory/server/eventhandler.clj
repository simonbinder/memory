(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.websocket :as websocket]))

(defn multicast-event-to-game [event game-id]
  (def game (get @games/games game-id))
  (def game-uids (vals (get game :players)))
    (doseq [uid game-uids]
          (println "uid" uid)
             (websocket/chsk-send! uid [event game])))

(defn send-error [event uid message]
  (websocket/chsk-send! uid [event message]))

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

(declare cards-match? filter-unresolved-cards filter-turned-cards)

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


(defn cards-match? [[card-one card-two]]
  (= (:url card-one) (:url card-two)))

(defn filter-unresolved-cards [deck]
  (filter #(= (% :resolved) 0) deck))

(defn filter-turned-cards [deck]
  (filter #(:turned %) deck))

(defn validate-player-action [sender-uid game]
  (if (not= sender-uid (get-active-player-uid game))
     (throw (.Exception (str "Event received from player "))))
)

(defn get-active-player-uid [game]
  ((:players game)(:active-player game))
)

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
