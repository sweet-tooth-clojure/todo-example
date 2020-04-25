(ns sweet-tooth.todo-example.backend.endpoint.todo-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datomic.api :as d]
            [sweet-tooth.endpoint.test.harness :as eth]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]
            [sweet-tooth.todo-example.backend.test.data :as td]
            [sweet-tooth.todo-example.backend.test.db :as tdb]))

(use-fixtures :each (eth/system-fixture :test))

(deftest creates-todo
  (let [{:keys [tl0]} (td/transact! {:todo-list [[1 {:spec-gen {:todo-list/title "GET EGGS"}}]]})
        t             {:todo/title "yeah eggs" :todo/todo-list tl0}
        resp-data     (-> (eth/req :post "/api/v1/todo" t)
                          (eth/read-body))]
    (is (eth/contains-entity? resp-data :todo (update t :todo/todo-list (fn [id] {:db/id id}))))))

(deftest updates-todo
  (let [{:keys [t0]} (td/transact! {:todo [[1 {:spec-gen {:todo/title "GET EGGS"}}]]})]
    (is (eth/contains-entity? (-> (eth/req :put (str "/api/v1/todo/" t0) {:todo/title "GET MILK"})
                                  (eth/read-body))
                              :todo
                              {:db/id t0 :todo/title "GET MILK"}))
    (is (= "GET MILK" (:todo/title (d/entity (tdb/db) t0))))))

(deftest deletes-todo
  (let [{:keys [t0 tl0]} (td/transact! {:todo [[1 {:spec-gen {:todo/title "GET EGGS"}}]]})]
    (eth/req :delete (str "/api/v1/todo/" t0))
    (is (empty? (t/todos-by-todo-list (tdb/db) tl0)))))
