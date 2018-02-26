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
              players (get server-game :players)
               active-player (get server-game :active-player)]
         (swap! model/app-state assoc :state 2)
         (if (= 2 (@model/app-state :turned-cards))
            (swap! model/app-state assoc :turned-cards 0))
         (swap! model/app-state assoc :player-number (get-player-number server-game))
         (println "Server-Game: " server-game)
         (swap! model/game assoc :deck deck)
         (swap! model/game assoc :active-player active-player)
         (swap! model/game assoc :players players)
         (let [[own-score opponent-score] (model/calc-game-count)]
         (swap! model/game-count assoc :own-score own-score)
         (swap! model/game-count assoc :opponent-score opponent-score))
         (println "Client-Game: "@model/game)
         (println "Game-count "@model/game-count)
         (println @model/app-state)))

(defn finish-game [server-game]
  (let [deck (get server-game :deck)
        players (get server-game :players)
         active-player (get server-game :active-player)]
   (swap! model/app-state assoc :state 4)
   (swap! model/game assoc :deck deck)
   (swap! model/game assoc :active-player active-player)
   (swap! model/game assoc :players players)
   (let [[own-score opponent-score] (model/calc-game-count)]
   (swap! model/game-count assoc :own-score own-score)
   (swap! model/game-count assoc :opponent-score opponent-score))))

(defn handle-error [error-message]
  (model/show-error error-message))

(defn set-to-wait []
        (swap! model/app-state assoc :state 3))
