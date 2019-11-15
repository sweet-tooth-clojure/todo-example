(ns sweet-tooth.todo-example.backend.endpoint.static-test
  (:require [sweet-tooth.todo-example.backend.endpoint.static :as sut]
            [sweet-tooth.endpoint.test.harness :as eth]
            [sweet-tooth.todo-example.backend.duct]
            [clojure.test :refer :all]
            [clojure.java.io :as io]))

(use-fixtures :each (eth/system-fixture :test))

(deftest public-routes
  (let [index-html (slurp (io/resource "public/index.html"))]
    (is (= index-html (slurp (:body (eth/req :get "/")))))))
