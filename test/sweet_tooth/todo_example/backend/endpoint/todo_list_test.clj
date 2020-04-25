(ns sweet-tooth.todo-example.backend.endpoint.todo-list-test
  (:require [sweet-tooth.todo-example.backend.test.data :as td]
            [sweet-tooth.endpoint.test.harness :as eth]
            [clojure.test :refer :all]))

(use-fixtures :each (eth/system-fixture :test))

(deftest list-todo-lists
  (let [title         "GET EGGS"
        {:keys [tl0]} (td/transact! {:todo-list [[1 {:spec-gen {:todo-list/title "GET EGGS"}}]]})
        resp-data     (-> (eth/req :get "/api/v1/todo-list")
                          (eth/read-body))]
    (is (eth/contains-entity? resp-data :todo-list {:todo-list/title title}))))


(deftest shows-todo-list-with-todos
  (let [{:keys [tl0 t0 t1 t2]} (td/transact! {:todo [[5]]})
        resp-data     (-> (eth/req :get (str "/api/v1/todo-list/" tl0))
                          (eth/read-body))]
    (is (eth/contains-entity? resp-data :todo-list {:db/id tl0}))
    (is (eth/contains-entity? resp-data :todo {:db/id t0}))
    (is (eth/contains-entity? resp-data :todo {:db/id t1}))
    (is (eth/contains-entity? resp-data :todo {:db/id t2}))))

(deftest creates-todo-list
  (let [tl            {:todo-list/title "GET EGGS"}
        resp-data     (-> (eth/req :post "/api/v1/todo-list" tl)
                          (eth/read-body))]
    (is (eth/contains-entity? resp-data :todo-list tl))))
