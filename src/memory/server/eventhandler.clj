(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game :as game]))

(defn multicast-event-to-game [event game-id]
  (let [game (:game-id @games)
        player-one ([:player-one :uid] game)
        player-two ([:player-two :uid] game)]
          (doseq [uid (:any [player-one player-two])]
             (websocket/chsk-send! uid event))))


(defn create-game [uid]
    (games/add-new-game uid))


(defn join-game-handler [uid game-id]
  ;; TODO ADD Error handling
  (games/add-player-to-game uid game-id)
  (multicast-event-to-game [:notification/user-connected uid] game-id)
  ;;TODO Send deck?
  )
