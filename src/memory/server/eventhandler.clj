(ns memory.server.eventhandler
  (:require
    [memory.server.games :as games]
    [memory.server.game :as game]))

(defn create-game [uid]
    (games/add-new-game uid))

(declare handle-card-selected validate-player-action get-active-player-uid)

(defn handle-card-selected [uid game]
  (validate-player-action uid game)
  (deter)

)

(declare determine-next-active-player determine-number-of-card-selected determine-game-state)
(defmethod determine-game-state)
(defmulti forward-game determine-game-state)
(defmethod forward-game :first-card-selected)
(defmethod forward-game :second-card-selected)
(defmethod forward-game :first-card-selected)



(validate-player-action [sender-uid game]
  (if (not= sender-uid (get-active-player-uid game))
     (throw (.Exception (str "Event received from player "))))
)

(defn get-active-player-uid [game]
  ((:players game)(:active-player game))
)
