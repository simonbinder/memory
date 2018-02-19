(ns memory.server.player-state-handler
  (:require
    [memory.server.games :as games]
    [memory.server.websocket :as websocket]))

(defn player-disconnected-handler [uid]
  (let [game-id (get @games/users uid)
    player-index (filter-players uid game-id)]
  (swap! games/games assoc-in [game-id :players player-index] nil)
 (swap! games/users dissoc uid)
 (if (game-nil? game-id)
 (swap! games/games dissoc game-id)
 (multicast-event-with-message :game/waiting-for-player
      "Waiting for second player to connect" game-id))))
