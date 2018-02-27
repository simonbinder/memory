(ns memory.server.eventrouter
 (:require
   [memory.server.eventhandler :as eventhandler]
   [memory.server.websocket :as websocket]))

;; --------------------------------- event routing ---------------------------------

(defmulti event :id)

(defmethod event :game/create-game [{:as ev-msg :keys [event uid client-id ?data ?reply-fn]}]
  (when ?reply-fn
    (?reply-fn (let [game-id (eventhandler/create-game-handler uid)]
      {:game-id game-id} ))))

(defmethod event :game/join-game [{:as ev-msg :keys [event uid client-id ?data ?reply-fn]}]
  (println "Join Game Called with data:")
  (println ?data)
  (println (:game-id ?data))
  (eventhandler/join-game-handler uid (:game-id ?data)))

(defmethod event :default [{:as ev-msg :keys [event]}]
  (println "Unhandled event: " event))

(defmethod event :game/selected-card [{:as event :keys [uid ?data]}]
  (eventhandler/card-selected-handler uid (:game ?data)))

(defmethod event :chsk/uidport-open [{:keys [uid client-id]}]
  (println "New connection:" uid client-id))

(defmethod event :chsk/uidport-close [{:keys [uid]}]
  (eventhandler/player-disconnected-handler uid)
  (println "Disconnected:" uid))

(defmethod event :chsk/ws-ping [_])
