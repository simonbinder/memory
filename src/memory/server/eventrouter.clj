(ns memory.server.eventrouter
 (:require
   [memory.server.eventhandler :as eventhandler]
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
    (?reply-fn (let [game-id (eventhandler/create-game uid)]
      {:game-id game-id} ))))

(defmethod event :default [{:as ev-msg :keys [event]}]
  (println "Unhandled event: " event))

;; ctrl , then shift b
(defmethod event :test/id1 [{:as ev-msg :keys [event uid client-id ?data]}]
  (println "Hello from User: " uid client-id ?data)
  (broadcast))

(defmethod event :chsk/uidport-open [{:keys [uid client-id]}]
    (println "New connection:" uid client-id))

(defmethod event :chsk/uidport-close [{:keys [uid]}]
      (println "Disconnected:" uid))

(defmethod event :chsk/ws-ping [_])