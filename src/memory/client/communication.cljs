(ns memory.client.communication ; .cljs
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
    [memory.client.eventhandler :as eventhandler]
   [cljs.core.async :as async :refer (<! >! put! chan)]
   [taoensso.sente  :as sente :refer (cb-success?)]))

(defn get-chsk-url
    "Connect to a configured server instead of the page host"
    [protocol chsk-host chsk-path type]
    (let [protocol (case type :ajax protocol
                              :ws   (if (= protocol "https:") "wss:" "ws:"))]
      (str protocol "//" "localhost:8080" chsk-path)))

(defonce channel-socket
    (with-redefs [sente/get-chsk-url get-chsk-url]
     (sente/make-channel-socket! "/chsk" {:type :auto})))
(defonce chsk (:chsk channel-socket))
(defonce ch-chsk (:ch-recv channel-socket))
(defonce chsk-send! (:send-fn channel-socket))
(defonce chsk-state (:state channel-socket))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default [{:as ev-msg :keys [event]}]
  (println "Unhandled event: %s" event))

(defmethod event-msg-handler :chsk/state [{:as ev-msg :keys [?data]}]
  (if (= ?data {:first-open? true})
    (println "Channel socket successfully established!")
    (println "Channel socket state change:" ?data)))

(defmethod event-msg-handler :chsk/recv
  ;; default custom push event. Unwraps the true message that, for
  ;; now, comes wrapped in sente.
  [{:as ev-msg :keys [?data]}]
  (let [[message-type message-payload] ?data]
     (case message-type
       :game/send-game-data (eventhandler/receive-game (nth ?data 1))
       :game/waiting-for-player (eventhandler/set-to-wait)
       :game/game-finished (eventhandler/finish-game (nth ?data 1))
       :error/game-not-found (eventhandler/handle-error (nth ?data 1))
       :error/too-many-players-in-game (eventhandler/handle-error (nth ?data 1))
       (println ?data))))

(defn send-game [client-game]
  (chsk-send! [:game/selected-card {:game client-game}]))

(defn create-game [start-game-reply]
  (chsk-send! [:game/create-game {:game "game"}] 8000 start-game-reply))

(defn join-game [game-id]
  (chsk-send! [:game/join-game {:game-id game-id}]))

(defmethod event-msg-handler :chsk/handshake [{:as ev-msg :keys [?data]}]
    (let [[?uid ?csrf-token ?handshake-data] ?data]
      (println "Handshake:" ?data)
      (eventhandler/set-uid ?uid)))

(defonce router
    (sente/start-client-chsk-router! ch-chsk event-msg-handler))
