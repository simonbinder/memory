(ns memory.server.event-sender
[memory.server.websocket :as websocket])

(defn multicast-event-to-game [event game]
  (let [game-uids (vals (get game :players))]
    (doseq [uid game-uids]
       (websocket/chsk-send! uid [event]))))

(defn multicast-event-with-message [event message game-id]
    (let [game (get @games/games game-id)
      game-uids (vals (get game :players))]
      (doseq [uid game-uids]
      (if-not (nil? uid)
        (websocket/chsk-send! uid [event message])))))

(defn multicast-game-to-players [game])
