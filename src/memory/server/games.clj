(ns memory.server.games
  (:require
    [memory.server.game :as game]))

(def games (atom {:queue '()}))

;;does this append the single elements or append the whole map?
(defn add-new-game [uid]
  (def game (game/create-new-game uid))
  (swap! games update-in [:queue] concat game)
  (println @games)
    game)
