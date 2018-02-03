(ns memory.server.games
  (:require
    [memory.server.game :as game]
    [digest :as digest]))
(def users (atom {}))
(def games (atom {}))

;; too long TODO: not random - same value always generates same id?
(defn create-game-id [uid]
  (digest/md5 uid))

;;does this append the single elements or append the whole map?
(defn add-new-game [uid]
  (let [game-id (create-game-id uid) game (game/create-new-game uid)]
    (swap! games assoc-in [game-id] game)
    (swap! users assoc-in [uid] game-id)
    {:game-id game-id :game game}
   ))
