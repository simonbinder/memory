(ns memory.server.event-sender
    (:require
        [memory.server.websocket :as websocket]))

(defn get-uids-of-game [game]
    (filter (vals (get game :players))))

(defn multicast-game-to-participants [game]
    (doseq [uid (get-uids-of-game game)]
        (websocket/chsk-send! uid [:game/send-game-data game])))

(defn multicast-event-to-participants-of-game [event-id game]
    (doseq [uid (get-uids-of-game game)]
        (websocket/chsk-send! uid [event-id game])))

(defn send-error-to-player [error-message uid]
    (websocket/chsk-send! uid [:error/game-not-found error-message]))
