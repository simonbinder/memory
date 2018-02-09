(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game :as game]))

(defn create-game [uid]
    (games/add-new-game uid))

(declare handle-card-selected validate-player-action does-sender-match-active-player?)

(defn handle-card-selected [uid game]
  ;(validate-player-action uid game))

(validate-player-action [uid game]
  )
