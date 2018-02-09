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

(defn create-game-handler [uid]
    (games/add-new-game uid))

(defn join-game-handler [uid game-id]
  ;; TODO ADD Error handling
  (games/add-player-to-game uid game-id)
  (def game (get @games/games game-id))
  (multicast-event-to-game :game/send-game-data game-id))
