(ns memory.server.game-logic-test
  (:require
    [memory.server.game-logic :refer :all]
    [clojure.test :refer :all]))

(declare get-card count-resolved-cards-of-player count-resolved-cards count-turned-cards)

(deftest test-determine-game-state
   (testing "FIRST CARD SELECTED: "
       (testing "Just one card turned."
           (let [game {:deck [(get-card)(get-card "file_one" true 0)]}]
               (is (= :first-card-selected (determine-game-state game))))))
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
                 (is (= :cards-not-matching (determine-game-state game)))))))

(deftest test-reset-turned-cards
    (testing "TEST RESET TURNED CARDS: Given three cards, when returned, then all Cards have (= :turned false)."
        (let [deck [(get-card "a" true 0)(get-card "b" true 0)(get-card "c" false 0)]
              actual (count (filter #(:turned %) (reset-turned-cards deck)))
              expected 0]
            (is (= actual expected)))))

(deftest change-active-player-test
    (testing "CHANGE ACTIVE PLAYER TEST: "
        (testing "If active player was one, it is changed two."
            (let [game {:active-player 1 :deck [(get-card)(get-card)]}
                  actual (-> game change-active-player :active-player)
                  expected 2]
                (is (= actual expected))))
        (testing "If active player was 2, it is changed to 1."
            (let [game {:active-player 2 :deck [(get-card)(get-card)]}
                  actual (-> game change-active-player :active-player)
                  expected 1]
                (is (= actual expected))))))

(deftest set-turned-cards-as-resolved-by-test
    (testing "SET TURNED CARDS AS RESOLVED BY: "
        (testing "Given three turned cards and one unturned, when player one is passed, all 3 turned cards are resolved by that user."
            (let [deck [(get-card "one" true 0)(get-card "two" true 1)(get-card "three" true 0)(get-card "four" false 2)]
                  actual (count (filter #(and (:turned %) (= 2 (:resolved %))) (set-turned-cards-as-resolved-by deck 2)))
                  expected 3]
                (is (= actual expected))))))

(deftest filter-unresolved-cards-test
    (testing "FILTER UNRESOLVED CARDS TEST: "
        (testing "When all cards have (not= resolved 0), then 0 cards are returned."
            (let [deck [(get-card "one" true 1)(get-card "two" false 2)]
                  actual (count (filter-unresolved-cards deck))
                  expected 0]
                (is (= actual expected))))
        (testing "Given three cards, when two cards have (= resolved 0), then both are returned."
            (let [deck [(get-card "Some")(get-card)(get-card)]
                  actual (-> deck filter-unresolved-cards count)
                  expected 2]
                (is (= actual expected))))))

(deftest game-finished?-test
    (testing "GAME FINISHED TEST: "
        (testing "Given three cards, when all cards are (not= resolved 0), then returns true."
            (let [game {:deck [(get-card "one" true 1)(get-card "two" false 2)(get-card "three" false 1)]}
                  actual (game-finished? game)
                  expected true]
            (is (= actual expected))))
        (testing "Given three cards, when one card is (= resolved 0), then returns false"
            (let [game {:deck [(get-card "one" true 1)(get-card "one" true 2)(get-card)]}
                  actual (game-finished? game)
                  expected false]
                (is (= actual expected))))))

(deftest cards-match?-test
    (testing "CARDS MATCH? TEST: "
        (testing "Given two cards, when both have same url, then return true."
            (let [cards [(get-card)(get-card)]
                  actual (cards-match? cards)
                  expected true]
                (is (= actual expected))))
        (testing "Given two cards, when urls differ, then return false."
            (let [cards [(get-card)(get-card "two" true 1)]
                  actual (cards-match? cards)
                  expected false]
                (is (= actual expected))))))

(deftest update-deck-in-game-test
    (testing "UPDATE DECK IN GAME TEST: "
        (testing "When game is updated with deck, then card of deck can be found in game."
            (let [game {:deck 'noDeck}
                  deck [(get-card "one" true 0)(get-card "two" false 1)]
                  actual (-> deck (update-deck-in-game game) :deck first :url)
                  expected "one"]
                (is (= actual expected))))))

(deftest forward-game-when-first-card-selected
    (testing "FORWARD GAME WHEN FIRST CARD SELECTED: "
        (testing "Nothing tested."
            (is false))))

(deftest forward-game-when-cards-matching
    (testing "FORWARD GAME WHEN CARDS MATCHING "
        (let [game {:active-player 1 :deck [(get-card)(get-card)(get-card "x" true 0)(get-card "x" true 0)]}
             new-game (forward-game-when game)]
            (testing "...new game contains 0 turned cards."
                (is (= (count-turned-cards (:deck new-game)) 0)))
            (testing "...new game contains +2 resolved cards for player that was active."
                (let [old-player (:active-player game)]
                    (is (=
                        (+ 2 (count-resolved-cards-of-player (:deck game) old-player))
                        (count-resolved-cards-of-player (:deck new-game) old-player)))))
            (testing "...active player not changed."
                (is (= (:active-player game) (:active-player new-game)))))))

(deftest forward-game-when-cards-not-matching
    (testing "FORWARD GAME WHEN CARDS NOT MATCHING "
        (let [game {:active-player 2 :deck [(get-card "x" true 0)(get-card "y" true 0)]}
             new-game (forward-game-when game)]
                (testing "...new game contains 0 turned cards. "
                    (is (= (count-turned-cards (:deck new-game)) 0)))
                (testing "...new game's resolved cards amount does not change."
                    (is (= (count-resolved-cards (:deck new-game)) (count-resolved-cards (:deck game)))))
                (testing "...active player changed from 2 to 1."
                    (is (1 (:active-player new-game)))))))

(deftest filter-turned-cards-test
    (testing "FILTER TURNED CARDS: "
        (testing "Given two cards, when one card is turned, only one is returned."
            (let [deck [(get-card)(get-card "one" true 0)]]
                (is (= 1 (count (filter-turned-cards deck))))))))

(defn count-resolved-cards-of-player [deck player]
    (count (filter #(= (:resolved %) player) deck)))

(defn count-resolved-cards [deck]
    (count (filter #(-> % :resolved (not= 0)) deck)))

(defn count-turned-cards [deck]
    (count (filter #(:turned %) deck)))

(defn get-card
  ([] (get-card "file_one" false 0))
  ([filename turned resolved]
    {
      :id (str (java.util.UUID/randomUUID))
      :url filename
      :turned turned
      :resolved resolved }))
