(ns sweet-tooth.todo-example.backend.endpoint.todo-list-test
  (:require [sweet-tooth.todo-example.backend.endpoint.todo-list :as sut]
            [sweet-tooth.todo-example.backend.test.data :as td]
            [sweet-tooth.endpoint.test.harness :as eth]
            [clojure.test :refer :all]))

(use-fixtures :each (eth/system-fixture :test))

(deftest list-todos
  (let [title         "GET EGGS"
        {:keys [tl0]} (td/transact! {:todo-list [[1 {:spec-gen {:todo-list/title "GET EGGS"}}]]})
        resp-data     (-> (eth/req :get "/api/v1/todo-list")
                          (eth/resp-read-transit))]

    (is (eth/contains-entity? resp-data :todo-list {:todo-list/title title}))))
