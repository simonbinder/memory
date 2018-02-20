(ns memory.server.game-logic-test
  (:require
    [memory.server.game-logic :refer :all]
    [clojure.test :refer :all]))

(declare get-card)

(deftest test-determine-game-state
   (testing "FIRST CARD SELECTED: "
       (testing "Just one card turned."
           (let [game {:deck [(get-card)(get-card "file_one" true 0)]}]
               (is (= :first-card-selected (determine-game-state game))))))
   (testing "Finished game: "
      (testing "Two unresolved cards left, both cards opened."
          (let [game {:deck [(get-card "file_one" true 0)(get-card "file_one" true 0)]}]
              (is (= :game-finished (determine-game-state game)))))
      (testing "4 unresolved cards left, two macthing cards turned."
          (let [game {:deck [(get-card "file_one" false 1)(get-card "file_one" false 1)(get-card "file_two" true 0)(get-card "file_two" true 0)]}]
              (is (= :game-finished (determine-game-state game))))))
    (testing "CARDS NOT MATCHING: "
       (testing "4 unresolved cards in deck, two cards turned and not matching."
           (let [game {:deck [(get-card)(get-card)(get-card "one" true 0)(get-card "two" true 0)]}]
               (is (= :cards-not-matching (determine-game-state game))))))
    (testing "CARDS MATCHING"
        (testing "Two Cards left, turned and matching."
            (let [game {:deck [(get-card "file_one" true 0)(get-card "file_one" true 0)]}]
                (is (= :cards-matching (determine-game-state game)))))
        (testing "Two Cards left, matching, but one turned."
            (let [game {:deck [(get-card "file_one" true 0)(get-card "file_one" false 0)]}]
                 (is (= :cards-not-matching (determine-game-state game)))))))))
(deftest test-reset-turned-cards
    (testing "All Cards have turned=false"
        (let [deck [(get-card "a" true 0)(get-card "b" true 0)(get-card "c" false 0)]]
            (is (= 0))))


(defn get-card
  ([] (get-card "file_one" false 0))
  ([filename turned resolved]
    {
      :id (str (java.util.UUID/randomUUID))
      :url filename
      :turned turned
      :resolved resolved }))
