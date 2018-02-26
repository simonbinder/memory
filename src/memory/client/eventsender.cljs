(ns memory.client.eventsender ; .cljs
  (:require
    [memory.client.model :as model]
    [memory.client.communication :as communication]))

(defn start-game-reply [reply]
  (let [game-id (get reply :game-id)]
  (if (nil? game-id)
    (model/show-error "No Server-Connection")
    (do
      (model/set-game-id game-id)
      (model/set-state 1)))))

(defn join-game [game-id]
  (model/set-game-id game-id)
  (communication/join-game game-id))

(defn handle-click []
  (communication/send-game @model/game))
