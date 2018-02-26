(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game-logic :as game-logic]
    [memory.server.event-sender :as event-sender]))

;;------------------- util-methods ------------------------
(defn get-player-for-uid-from-game [uid game-id]
  (first (filter (comp #{uid}  (get (games/get-game game-id) :players))
    (keys (get (games/get-game game-id) :players)))))




;; ------------- handler ---------------------------------

(declare no-player-left?)

(defn create-game-handler [uid]
  (if-let [game-id (games/get-game-id-for-uid uid)]
    (event-sender/send-error-to-player [:error/one-player-two-games(str "You are already associated with this game: " game-id ".")] uid)
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

(defn no-player-left? [game]
  (every? nil? (-> game :players vals)))

(defn join-game-handler [uid game-id]
  (if-let [game (-> game-id games/get-game)]
    (try
      (let [updated-game (games/add-player-to-game uid game)]
        (event-sender/multicast-game-to-participants :game/send-game-data updated-game)
        (games/update-game game-id updated-game)
        (games/update-users-game-id uid game-id))
      (catch Exception e (event-sender/send-error-to-player [:error/too-many-players-in-game (.getMessage e)] uid)))
    (event-sender/send-error-to-player [:error/game-not-found "Game does not exist"] uid)))

(defn card-selected-handler [uid client-game]
  (event-sender/multicast-game-to-participants :game/send-game-data client-game)
  (let [updated-game (game-logic/forward-game-when client-game)
        event-type (if (game-logic/game-finished? updated-game)
                             :game/game-finished
                             :game/send-game-data)]
     (println updated-game)
     (games/update-game (games/get-game-id-for-uid uid) updated-game)
     (event-sender/multicast-game-to-participants event-type updated-game)))

;; For testing demo
(defn four [] 4)
(defn match [[card-one card-two]]
   (= (:value card-one) (:value card-two)))
