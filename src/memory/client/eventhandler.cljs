(ns memory.client.eventhandler ; .cljs
  (:require
    [memory.client.model :as model]))

(defn get-player-number[game]
      (let [uid (:player-uid @model/app-state)
        players (get game :players)
       player-number (first (filter (comp #{uid} players) (keys players)))]
      player-number))

(defn set-uid [uid]
  (swap! model/app-state assoc :player-uid uid))

(defn receive-game [server-game]
        (let [deck (get server-game :deck)
               active-player (get server-game :active-player)]
         (swap! model/app-state assoc :state 2)
         (swap! model/app-state assoc :player-number (get-player-number server-game))
         (println "Server-Game: " server-game)
         (swap! model/game assoc :deck deck)
         (swap! model/game assoc :active-player active-player)
         (println "Client-Game: "@model/game)
         (println @model/app-state)))
