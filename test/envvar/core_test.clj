(ns envvar.core-test
  (:require [clojure.test :refer :all]
            [envvar.core :as envvar :refer [env with-env]]))

(deftest basics
  (testing "env variables"
    (is (= (:user @env) (System/getenv "USER")))
    (is (= (:java-arch @env) (System/getenv "JAVA_ARCH"))))
  (testing "system properties"
    (is (= (:user-name @env) (System/getProperty "user.name")))
    (is (= (:user-country @env) (System/getProperty "user.country")))))

(deftest augmenting-env
  (is (nil? (:one @env)))
  (with-env [:one 1
             :two 2]
    (is (= 1 (:one @env)))
    (is (= 2 (:two @env))))
  (is (nil? (:one @env))))

(deftest file-vars
  (let [t1 (java.io.File/createTempFile "env" ".properties")]
    (try
      (spit t1 "ONE=1\nTWO=2")
      (envvar/load-file-variables! (.getAbsolutePath t1))
      (is (= "1" (:one @env)))
      (is (= "2" (:two @env)))
      (envvar/reset-file-variables!)
      (is (nil? (:one @env)))
      (is (nil? (:two @env)))
      (finally
        (.delete t1)))))
