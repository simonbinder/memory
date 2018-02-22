(ns memory.server.eventhandler-test
  (:require
    [memory.server.eventhandler :refer :all]
    [memory.server.event-sender]
    [memory.server.games :refer :all]
    [clojure.test :refer :all]))


;; TESTS ------------------------

(declare get-card multicast-event-to-game-dummy)

(defn get-game-id-for-uid-dummy [uid]
    (if (>= uid 0)
        nil
        1))

(defn add-new-game-dummy [uid] uid)

(defn send-error-to-player-dummy [message] message)

(deftest create-game-handler-test
    (testing "CREATE GAME HANDLER: "
        (binding [get-game-id-for-uid get-game-id-for-uid-dummy
                  add-new-game add-new-game-dummy
                  send-error-to-player send-error-to-player-dummy]
            (testing "When called with non-pre-existing uid, then games/add-new-game is called with given uid."
                (let [expected-uid 10
                      actual-uid create-game-handler expected-uid]
                    (is (= actual-uid expected-uid))))
            (testing "When called with pre-existing-uid, then event-sender/send-error-to-player is called with expected message and game-id."
                (let [expected "You are already associated with this game: 1."
                      actual create-game-handler -1])))))

;(deftest card-selected-handler-test ())

;; HELPERS --------------------------------
(defn multicast-event-to-game-dummy [event-id game-id](def dummy-fn-params [event-id game-id]))

(defn count-turned-cards [deck] (count (filter #(:turned %) deck)))
(defn count-resolved-cards-of-player [deck player](count (filter #(= (:resolved %) player) deck)))

(defn create-game-dummy [] {
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
