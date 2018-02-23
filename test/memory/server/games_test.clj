(ns memory.server.games-test
    (:require
        [memory.server.games :refer :all]
        [clojure.test :refer :all]))

(deftest add-player-to-game-add-second-player-to-existing-game-test
    (testing "Given one existing game, when player is added to game, entry for second player is created at player-index 2."

        (let [game-id (add-new-game ["uid-a"])
              users (add-player-to-game "uid-b" game-id)
              expected "uid-b"
              actual (get-in @games [game-id :players 2])]
        (println actual)
        (is (= actual expected)))))

(defn clean-users []
    (let [uids (keys @users)]
        (doseq [uid uids]
            (swap! users dissoc uid))))

(defn clean-games []
    (let [game-ids (keys @games)]
        (doseq [game-id game-ids]
            (swap! games dissoc game-id))))

(defn clean-up-fixture [f]
  (f)
  (clean-users)
  (clean-games)
 )


(use-fixtures :each clean-up-fixture)
