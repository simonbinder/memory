(ns memory.server.eventhandler-test
  (:require
    [memory.server.eventhandler :refer :all]
    [memory.server.games :refer :all]
    [clojure.test :refer :all]))


;; TESTS ------------------------

(declare get-card multicast-event-to-game-dummy)

(deftest test-determine-game-state

   (testing "Finished game: "
      (testing "Two unresolved cards left, one card turned."
          (let [game {:deck [(get-card)(get-card "file_one" true 0)]}]
              (is (= :first-card-selected (determine-game-state game)))))
      (testing "Two unresolved cards left, both cards opened."
          (let [game {:deck [(get-card "file_one" true 0)(get-card "file_one" true 0)]}]
              (is (= :game-finished (determine-game-state game)))))
      (testing "4 unresolved cards left, two macthing cards turned."
          (let [game {:deck [(get-card "file_one" false 1)(get-card "file_one" false 1)(get-card "file_two" true 0)(get-card "file_two" true 0)]}]
              (is (= :game-finished (determine-game-state game))))))
    (testing "CARDS NOT MATCHING: "
       (testing "4 unresolved cards in deck, two cards turned and not matching."
           (let [game {:deck [(get-card)(get-card)(get-card "one" true 0)(get-card "two" true 0)]}]
               (is (= :cards-not-matching (determine-game-state game)))))))


(deftest filter-unresolved-cards-test
   (testing "FILTER UNRESOLVED CARDS: "
       (testing "one resolved. one unresolved."
            (let [cards [(get-card) (get-card "any" false 1)]
                  expected (vals (first cards))
                  actual (vals (first (filter-unresolved-cards cards)))]
                 (println actual)
                 (is (= expected actual))))))

(deftest filter-turned-cards-test
    (testing "FILTER TURNED CARDS: "
        (testing "One turned, one not turned."
            (let [deck [(get-card)(get-card "one" true 0)]]
                (is (= 1 (count (filter-turned-cards deck))))))))

(deftest forward-game-when-first-card-selected
    (testing "FORWARD GAME WHEN FIRST CARD SELECTED"
        (testing "Game is multicasted to Clients."
            (declare dummy-fn-params)
            (let game {:deck [(get-card "y" false 0)(get-card "x" true 0)]}]
                      (forward-game-when multicast-event-to-game-dummy game TODOmissingparam)
                      (is (= [:game/send-game-data new-game] dummy-fn-params))))))

(deftest forward-game-when-cards-matching
    (testing "FORWARD GAME WHEN CARDS MATCHING "
        (declare dummy-fn-params)
        (let [game {:deck [(get-card)(get-card)(get-card "x" true 0)(get-card "x" true 0)]}
             new-game (forward-game-when multicast-event-to-game-dummy game TODOMissingParam)]
            (testing "...game is multicasted to clients. "
                (is (= [:game/send-game-data new-game] dummy-fn-params)))
            (testing "...new game contains 0 turned cards."
                (is (= (count-turned-cards (:deck new-game)) (count-turned-cards (:deck game)))))
            (testing "...new game contains +2 resolved cards for player that was active."
                (let [old-player (:active-player game)]
                    (is (= (+ 2 (count-resolved-cards-of-player (:deck game) old-player)) (count-resolved-cards-of-player (:deck new-game) old-player)))))
            (testing "...active player not changed."
                (is (= (:active-player game) (:active-player new-game))))))

(deftest forward-game-when-cards-matching
    (testing "FORWARD GAME WHEN CARDS NOT MATCHING "
        (declare dummy-fn-params)
        (let [game {:deck [(get-card "x" true 0)(get-card "y" true 0)]}
             new-game (forward-game-when multicast-event-to-game-dummy game TODOMissingParam)]
                (testing "...game is multicast to clients"
                    (is (= dummy-fn-params [:game/send-game-data new-game])))
                (testing "...new game contains 0 turned cards. "
                    (is (= (count-turned-cards (:deck new-game)) (count-turned-cards (:deck game)))))
                (testing "...new game's resolved cards amount does not change."
                    (is (= (count-resolved-cards (:deck new-game)) (count-resolved-cards (:deck game)))))
                (testing "...active player changed."
                    (is (not= (:active-player game) (:active-player new-game)))))))

(deftest forward-game-when-game-finished
    (testing "FORWARD GAME WHEN GAME FINISHED"
        (declare dummy-fn-params)
        (let [game {:deck [(get-card "x" false 1)(get-card "x" false 1)(get-card "y" true 0)(get-card "y" true 0)]}
             new-game (forward-game-when multicast-event-to-game-dummy game TODOMissingParam)]
                 (testing "...game is multicasted to clients with game-finished event."
                     (is (= dummy-fn-params [:game/game-finished new-game])))
                 (testing "...new game contains 0 turned cards."
                     (is (= (count-turned-cards (:deck new-game)) (count-turned-cards (:deck game)))))
                 (testing "...new game contains +2 resolved cards for player that was active."
                     (let [old-player (:active-player game)]
                         (is (= (+ 2 (count-resolved-cards-of-player (:deck game) old-player)) (count-resolved-cards-of-player (:deck new-game) old-player)))))))

;; HELPERS --------------------------------
(defn multicast-event-to-game-dummy [event-id game-id](def dummy-fn-params [event-id game-id]))

(defn count-turned-cards [deck] (count (filter #(:turned %) deck)))
(defn count-resolved-cards-of-player [deck player](count (filter #(= (:resolved %) player)) deck))

(defn get-game [] {
    :deck [(get-card)(get-card)]})

(defn get-card
  ([] (get-card "file_one" false 0))
  ([filename turned resolved]
    {
      :id (str (java.util.UUID/randomUUID))
      :url filename
      :turned turned
      :resolved resolved }))



;; Examples ---------------------------------------------------


;; to run test, run 'lein test' at the projects root
;;Example testcase using function four from eventhandler.clj
(comment
 "(deftest test-four ;; the testname
 (testing "four is four"
   (is (= 4 (four)))))

(deftest test-cards-match?
  (testing "test-match"
  (declare cards)
  (is (false? (cards-match? cards)))))

(def my-fixture [test]
   (def cards [{:value 1}{:value 2}])
   (test)
)"
)
;;uncomment to use fixtures
;;(use-fixtures :each my-fixture)
