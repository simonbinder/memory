(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.websocket :as websocket]))

;;----------- send-methods ------------------------
(defn multicast-event-to-game [event game-id]
  (def game (get @games/games game-id))
  (def game-uids (vals (get game :players)))
    (doseq [uid game-uids]
       (websocket/chsk-send! uid [event game])))

(defn multicast-event-with-message [event message game-id]
    (def game (get @games/games game-id))
    (def game-uids (vals (get game :players)))
      (doseq [uid game-uids]
      (if-not (nil? uid)
        (websocket/chsk-send! uid [event message]))))

;;------------------- util-methods ------------------------
(defn filter-players [uid game-id]
    (first (filter (comp #{uid}  (get (get @games/games game-id) :players))
      (keys (get (get @games/games game-id) :players)))))

(defn game-nil? [game-id]
  (every? empty? (vals (select-keys
        (get (get @games/games game-id) :players) [1 2]))))

;; ------------- handler ---------------------------------
(defn player-disconnected-handler [uid]
  (def game-id (get @games/users uid))
  (def player-index (filter-players uid game-id))
  (swap! games/games assoc-in [game-id :players player-index] nil)
 (swap! games/users dissoc uid)
 (if (game-nil? game-id)
 (swap! games/games dissoc game-id)
 (multicast-event-with-message :game/waiting-for-player
      "Waiting for second player to connect" game-id)))

(defn send-error [event uid message]
  (websocket/chsk-send! uid [event message]))

(defn create-game-handler [uid]
     (games/add-new-game uid))

(defn join-game-handler [uid game-id]
  (if (nil? (get @games/games game-id))
    ((send-error :error/game-not-found uid "Game does not exist")
    (throw (Exception. "Game does not exist."))))
  (games/add-player-to-game uid game-id)
  (def game (get @games/games game-id))
  (multicast-event-to-game :game/send-game-data game-id))


;; For testing demo

(defn four [] 4)
(defn match [[card-one card-two]]
   (= (:value card-one) (:value card-two)))
