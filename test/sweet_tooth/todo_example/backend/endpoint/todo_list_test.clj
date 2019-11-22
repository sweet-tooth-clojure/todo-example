(ns sweet-tooth.todo-example.backend.endpoint.todo-list-test
  (:require [sweet-tooth.todo-example.backend.endpoint.todo-list :as sut]
            [sweet-tooth.endpoint.test.harness :as eth]
            [clojure.test :refer :all]))

(deftest list-todos
  (let [{:keys [tl0]} (td/transact! {:todo-list [[1]]})
        resp-data    (-> (eth/req :get "/api/v1/todo-list")
                         (eth/resp-read-transit))]

    (is (eth/contains-entity? resp-data :post {:post/content post-content}))))
