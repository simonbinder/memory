(ns memory.server.eventhandler-test
  (:require
    [memory.server.eventhandler :refer :all]
    [memory.server.games :refer :all]
    [clojure.test :refer :all]))

;; TESTS ------------------------

(declare get-card)

(deftest test-determine-game-state

   (testing "finished game"
      (testing "Only One Card Opened, two cards left."
          (let [game {:deck [(get-card)(get-card "file_one" true 0)]}]
              (is (= :first-card-selected (determine-game-state game)))))
      (testing "both cards opened, only one pair left."
          (let [game {:deck [(get-card "file_one" true 0)(get-card "file_one" true 0)]}]
              (is (not= :finished (determine-game-state game)))))
      (testing "2 pairs left, two cards left and turned."
          (let [game {:deck [(get-card "file_one" false 1)(get-card "file_one" false 1)(get-card "file_two" true 0)(get-card "file_two" true 0)]}]
              (is (= :finished (determine-game-state game)))))))


(deftest filter-unresolved-cards-test
   (testing "FILTER UNRESOLVED CARDS: "
       (testing "one resolved. one unresolved."
            (let [cards [(get-card) (get-card "any" false 1)]
                  expected (vals (first cards))
                  actual (vals (first (filter-unresolved-cards cards)))]
                 (println actual)
                 (is (= expected actual))))))

(deftest)

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
 (deftest test-four ;; the testname
 (testing "four is four"
   (is (= 4 (four)))))

(deftest test-cards-match?
  (testing "test-match"
  (declare cards)
  (is (false? (cards-match? cards)))))

(def my-fixture [test]
   (def cards [{:value 1}{:value 2}])
   (test)
)
)
;;uncomment to use fixtures
;;(use-fixtures :each my-fixture)
