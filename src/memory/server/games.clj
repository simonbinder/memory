(ns memory.server.games
  (:require
    [memory.server.game :as game]
    [digest :as digest]))
(def users (atom {}))
(def games (atom {}))

;; too long TODO: not random - same value always generates same id?
(defn create-game-id [uid]
  (digest/md5 uid))

(defn player-nil? [player-key game-id]
  (nil? ([game-id player-key :uid])))



(defn add-player-to-game [uid game-id]
  (if (player-nil? [:player-two game-id])
    (swap! games assoc-in [game-id :player-two :uid] uid)
    (if (player-nil? [:player-one game-id])
       (swap! games assoc-in [game-id :player-one :uid] uid)
       (throw (Exception. "There are already two players participating in this game.")))))

;;does this append the single elements or append the whole map?
(defn add-new-game [uid]
  (let [game-id (create-game-id uid) game (game/create-new-game uid)]
    (swap! games assoc-in [game-id] game)
    (swap! users assoc-in [uid] game-id)
    game-id
   ))
