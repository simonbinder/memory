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

(defn filter-players [uid game-id]
    (first (filter (comp #{uid}  (get (get @games/games game-id) :players))
      (keys (get (get @games/games game-id) :players)))))

(defn player-disconnected [uid]
  (def game-id (get @games/users uid))
  (def player-index (filter-players uid game-id))
  (swap! games/games assoc-in [game-id :players player-index] nil)
 (swap! games/users dissoc uid))

(defn join-game-handler [uid game-id]
  ;; TODO ADD Error handling
  (games/add-player-to-game uid game-id)
  (def game (get @games/games game-id))
  (multicast-event-to-game :game/send-game-data game-id))
