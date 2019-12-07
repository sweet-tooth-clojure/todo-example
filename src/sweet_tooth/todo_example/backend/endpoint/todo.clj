(ns sweet-tooth.todo-example.backend.endpoint.todo
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.utils :as eu]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]))

(defn result-todos
  [ctx]
  (t/todos-by-todo-list (ed/db-after ctx) (:todo/todo-list (el/params ctx))))

(def decisions
  {:create {:post!          (fn [ctx]
                              (-> @(d/transact (ed/conn ctx)
                                               [(merge {:db/id (d/tempid :db.part/user)}
                                                       (el/params ctx))])
                                  (el/->ctx :result)))
            :handle-created result-todos}

   :update {:put!      (comp #(el/->ctx % :result) deref ed/update)
            :handle-ok result-todos}})
