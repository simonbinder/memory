(ns memory.client.eventhandler ; .cljs
  (:require
    [memory.client.model :as model]))

(defn set-uid [uid]
  (swap! model/app-state assoc :player-uid uid))

(defn receive-game [server-game]
  (model/set-state 2)
  (model/set-player-number server-game)
  (model/set-game server-game))

(defn finish-game [server-game]
  (model/set-state 4)
  (model/set-game server-game))

(defn handle-error [error-message]
  (model/show-error error-message))

(defn set-to-wait []
  (model/set-state 3))
