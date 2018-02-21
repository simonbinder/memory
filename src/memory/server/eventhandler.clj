(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game-logic :as game-logic]
    [memory.server.event-sender :as event-sender]))

;;------------------- util-methods ------------------------
(defn get-player-for-uid-from-game [uid game-id]
    (first (filter (comp #{uid}  (get (games/get-game game-id) :players))
      (keys (get (games/get-game game-id) :players)))))


(defn no-player-left? [game]
  (every? nil? (-> game :players vals)))

;; ------------- handler ---------------------------------

(defn create-game-handler [uid]
    (if-let [game-id (games/get-game-id-for-uid uid)]
        (event-sender/send-error-to-player (str "You are already associated with this game: " game-id "."))
        (games/add-new-game uid)))

(defn player-disconnected-handler [uid]
    (let [game-id (get @games/users uid)
          player-index (get-player-for-uid-from-game uid game-id)
          game (-> game-id
                   games/get-game
                   (assoc-in [:players player-index] nil))]
          (swap! games/users dissoc uid)
          (if (no-player-left? game)
              (-> game-id games/remove-game)
              (do
                  (games/update-game game-id game)
                  (event-sender/multicast-event-to-participants-of-game [:game/waiting-for-player "Waiting for second player to connect"] game)))))

(defn join-game-handler [uid game-id]
  (let [game (-> game-id games/get-game)]
      (if (nil? game)
         (event-sender/send-error-to-player "Game does not exist")
            ;(throw (Exception. "Game does not exist."))))
         (do
             (games/add-player-to-game uid game-id)
             (event-sender/multicast-event-to-participants-of-game :game/send-game-data game)))))

(defn card-selected-handler [client-game]
  (let [updated-game (game-logic/forward-game-when client-game)
        event-type (if (game-logic/game-finished? updated-game)
                             :game/game-finished
                             :game/send-game-data)]
       (games/update-game updated-game)
       (event-sender/multicast-event-to-participants-of-game event-type updated-game)))


;; For testing demo

(defn four [] 4)
(defn match [[card-one card-two]]
   (= (:value card-one) (:value card-two)))
