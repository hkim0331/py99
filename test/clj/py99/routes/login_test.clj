(ns py99.routes.login-test
  (:require [clojure.test :refer [deftest testing is]]
            [py99.routes.login :refer :all]))

(deftest get-user-test
 (testing "get-user"
  (is (= "hkimura" (:login (get-user "hkimura"))))
  (is (= nil (get-user "some-user")))))
 