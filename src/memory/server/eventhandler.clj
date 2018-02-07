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

(defn join-game-handler [uid game-id]
  ;; TODO ADD Error handling
  (def game (games/add-player-to-game uid game-id))
  (multicast-event-to-game [:game/player-joined-game {
                                 :uid uid
                                 :game game } game-id)
  (multicast-event-to-game [:game/player-selected (get-in game [:turn :selected-player])])
  ;; is this the second event necessary? Clients can read, who is activated.
  )
