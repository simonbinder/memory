(ns memory.client.eventhandler ; .cljs
  (:require
    [memory.client.model :as model]))

(defn receive-game [server-game]
        (let [deck (get server-game :deck)
               active-player (get server-game :active-player)]
         (swap! model/app-state assoc :state 2)
         (println "Server-Game: " server-game)
         (swap! model/game assoc :deck deck)
         (swap! model/game assoc :active-player active-player)
         (println "Client-Game: "@model/game)
         (println @model/app-state)))
