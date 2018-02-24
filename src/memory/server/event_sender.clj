(ns memory.server.event-sender
    (:require
        [memory.server.websocket :as websocket]))

(defn get-uids-of-game [game]
    (filter #(some? %)(vals (get game :players))))

(defn multicast-game-to-participants [event-id game]
    (println (get-uids-of-game game))
    (doseq [uid (get-uids-of-game game)]
        (println (str "Sending game to uid: " uid))
        (websocket/chsk-send! uid [event-id game])))

(defn multicast-event-to-participants-of-game [event game]
    (doseq [uid (get-uids-of-game game)]
        (websocket/chsk-send! uid event)))

(defn send-error-to-player [error-message uid]
    (websocket/chsk-send! uid [:error/game-not-found error-message]))
