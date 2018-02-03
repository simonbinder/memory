(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game :as game]))

(defn create-game [uid]
    (games/add-new-game uid))
