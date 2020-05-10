(ns sweet-tooth.todo-example.backend.endpoint.todo-list-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [sweet-tooth.endpoint.test.harness :as eth]
            [sweet-tooth.todo-example.backend.test.data :as td]
            [sweet-tooth.todo-example.backend.test.db :as tdb]))

(use-fixtures :each (eth/system-fixture :test))

(deftest list-todo-lists
  (let [tl        {:todo-list/title "GET EGGS"}
        _         (td/transact! {:todo-list [[1 {:spec-gen tl}]]})
        resp-data (-> (eth/req :get "/api/v1/todo-list")
                      (eth/read-body))]
    (eth/assert-response-contains-one-entity-like resp-data tl :todo-list)))


(deftest shows-todo-list-with-todos
  (let [{:keys [tl0 t0 t1 t2]} (td/transact! {:todo [[5]]})
        resp-data     (-> (eth/req :get (str "/api/v1/todo-list/" tl0))
                          (eth/read-body))]
    (eth/assert-response-contains-entity-like resp-data {:db/id tl0} :todo-list)
    (eth/assert-response-contains-entity-like resp-data {:db/id t0} :todo)
    (eth/assert-response-contains-entity-like resp-data {:db/id t1} :todo)
    (eth/assert-response-contains-entity-like resp-data {:db/id t2} :todo)))

(deftest updates-todo-list
  (let [{:keys [tl0]} (td/transact! {:todo-list [[1 {:spec-gen {:todo-list/title "GET EGGS"}}]]})]
    (eth/assert-response-contains-one-entity-like (-> (eth/req :put (str "/api/v1/todo-list/" tl0) {:todo-list/title "GET MILK"})
                                                      (eth/read-body))
                                                  {:db/id tl0 :todo-list/title "GET MILK"}
                                                  :todo-list)
    (is (= "GET MILK" (:todo-list/title (d/entity (tdb/db) tl0))))))

(deftest creates-todo-list
  (let [tl            {:todo-list/title "GET EGGS"}
        resp-data     (-> (eth/req :post "/api/v1/todo-list" tl)
                          (eth/read-body))]
    (eth/assert-response-contains-one-entity-like resp-data tl :todo-list)))


(deftest demo-failing-create-test
  (let [resp-data (-> (eth/req :post "/api/v1/todo-list" {:todo-list/title "GET EGGS"})
                      (eth/read-body))]
    (eth/assert-response-contains-one-entity-like resp-data {:todo-list/title "GET MILK"} :todo-list)))
