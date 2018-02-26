(ns memory.server.eventrouter
 (:require
   [memory.server.eventhandler :as eventhandler]
   ;;TODO Move websocket to eventhandler ns
   [memory.server.websocket :as websocket])
)


(defn broadcast []
  (doseq [uid (:any @websocket/connected-uids)]
    (websocket/chsk-send! uid [:test-push/hello "Hello Test!"])))

;; Just added for testing
(defn broadcast-2 []
    (doseq [uid (:any @websocket/connected-uids)]
      (websocket/chsk-send! uid [:test-push/bye "Bye!"])))

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

;; ctrl , then shift b
(defmethod event :test/id1 [{:as ev-msg :keys [event uid client-id ?data]}]
  (println "Hello from User: " uid client-id ?data)
  (broadcast))

(defmethod event :game/selected-card [{:as event :keys [uid ?data]}]
  (print "event-router selected-card")
   (eventhandler/card-selected-handler [uid (:game ?data)]))

(defmethod event :chsk/uidport-open [{:keys [uid client-id]}]
    (println "New connection:" uid client-id))

(defmethod event :chsk/uidport-close [{:keys [uid]}]
      (eventhandler/player-disconnected-handler uid)
      (println "Disconnected:" uid))

(defmethod event :chsk/ws-ping [_])
