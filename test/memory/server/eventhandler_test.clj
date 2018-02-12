(ns memory.server.eventhandler-test
  (:require
    [memory.server.eventhandler :refer :all]
    [memory.server.games :refer :all]
    [clojure.test :refer :all]))


;; TESTS ------------------------

(declare get-card)

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

;; HELPERS --------------------------------
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
