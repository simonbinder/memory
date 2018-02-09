(ns memory.server.eventhandler-test
  (:require
    [memory.server.eventhandler :refer :all]
    [clojure.test :refer :all]))

;; to run test, run 'lein test' at the projects root
;;Example testcase using function four from eventhandler.clj
(deftest test-four ;; the testname
 (testing "four is four"
   (is (= 4 (four)))))

(deftest test-match
  (testing "test-match"
  (declare cards)
  (is (true? (match cards)))))

(defn my-fixture [test]
 (def cards [{:value 1} {:value 2}])
 (test))

(use-fixtures :each my-fixture)
