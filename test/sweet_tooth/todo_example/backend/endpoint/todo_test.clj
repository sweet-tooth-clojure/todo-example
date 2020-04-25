(ns sweet-tooth.todo-example.backend.endpoint.todo-test
  (:require [sweet-tooth.todo-example.backend.test.data :as td]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]
            [sweet-tooth.todo-example.backend.test.db :as tdb]
            [sweet-tooth.endpoint.test.harness :as eth]
            [clojure.test :refer [deftest is use-fixtures]]))

(use-fixtures :each (eth/system-fixture :test))

(deftest creates-todo
  (let [{:keys [tl0]} (td/transact! {:todo-list [[1 {:spec-gen {:todo-list/title "GET EGGS"}}]]})
        t             {:todo/title "yeah eggs" :todo/todo-list tl0}
        resp-data     (-> (eth/req :post "/api/v1/todo" t)
                          (eth/read-body))]
    (is (eth/contains-entity? resp-data :todo (update t :todo/todo-list (fn [id] {:db/id id}))))))

(deftest deletes-todo
  (let [{:keys [t0 tl0]} (td/transact! {:todo [[1 {:spec-gen {:todo/title "GET EGGS"}}]]})]
    (eth/req :delete (str "/api/v1/todo/" t0))
    (is (empty? (t/todos-by-todo-list (tdb/db) tl0)))))
