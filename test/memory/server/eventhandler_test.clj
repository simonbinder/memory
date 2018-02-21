(ns memory.server.eventhandler-test
  (:require
    [memory.server.eventhandler :refer :all]
    [memory.server.games :refer :all]
    [clojure.test :refer :all]))


;; TESTS ------------------------

(declare get-card multicast-event-to-game-dummy)



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
