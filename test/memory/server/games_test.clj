(ns memory.server.games-test
    (:require
        [memory.server.games :refer :all]
        [clojure.test :refer :all]))

(deftest add-player-to-game-add-second-player-to-existing-game-test
    (testing "Given one existing game, when player two is added to game, then entry for second player is created at index 2."
        (let [game-id (add-new-game "uid-a")
              game (add-player-to-game "uid-b" game-id)
              expected "uid-b"
              actual (get-in @games [game-id :players 2])]
        (is (= actual expected))))
    (testing "Given existing game, when player one is missing, then entry for second player is created at index 1"
        (let [game-id (-> @games keys first)
              uid (swap! games assoc-in [game-id :players 1] nil)
              game (add-player-to-game "uid-c" game-id)
              expected "uid-c"
              actual (get-in @games [game-id :players 1])]
            (is (= actual expected)))))

(deftest add-player-to-game-add-third-player-to-existing-game-test
    (testing "Given one existing game with two players, when third player is added to game, then throw exception."
        (let [game-id (add-new-game "uid-a")
              game (add-player-to-game "uid-b" game-id)]
            (is (thrown? Exception (add-player-to-game "uid-c" game-id))))))

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
