(ns sweet-tooth.todo-example.backend.endpoint.todo-test
  (:require [sweet-tooth.todo-example.backend.test.data :as td]
            [sweet-tooth.endpoint.test.harness :as eth]
            [clojure.test :refer :all]))

(use-fixtures :each (eth/system-fixture :test))

(deftest creates-todo
  (let [{:keys [tl0]} (td/transact! {:todo-list [[1 {:spec-gen {:todo-list/title "GET EGGS"}}]]})
        t             {:todo/title "yeah eggs" :todo/todo-list tl0}
        resp-data     (-> (eth/req :post "/api/v1/todo" t)
                          (eth/resp-read-transit))]
    (is (eth/contains-entity? resp-data :todo t))))
