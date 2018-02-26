(ns memory.client.eventsender ; .cljs
  (:require
    [memory.client.model :as model]
    [memory.client.communication :as communication]))

(defn set-game-id [game-id]
  (swap! model/app-state assoc :game-id (str game-id)))

(defn start-game-reply [reply]
  (let [game-id (get reply :game-id)]
  (if (nil? game-id)
    (model/show-error "No Server-Connection")
    (do
      (set-game-id game-id)
      (swap! model/app-state assoc :state 1)))))

(defn join-game-reply [reply]
  ;(swap! model/app-state assoc :state 2)
  ;(let [deck (get (get reply 2) :deck)]
  (print "join-game-reply" reply)
  ;(print "deck" deck)
  ;(swap! game assoc :deck deck)
  )

(defn join-game [game-id]
  (print game-id)
  (set-game-id game-id)
  (communication/join-game game-id join-game-reply))

;; not implemented yet
(defn handle-click []
  (communication/send-game @model/game))
