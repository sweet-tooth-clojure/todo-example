(ns sweet-tooth.todo-example.backend.endpoint.todo-list
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.utils :as eu]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]))

(def decisions
  {:list {:handle-ok (fn [ctx] (tl/todo-lists (ed/db ctx)))}
   :show {:handle-ok (fn [ctx]
                       (let [todo-list-id (ed/ctx-id ctx)
                             db           (ed/db ctx)]
                         [(into {:db/id todo-list-id} (d/entity db todo-list-id))
                          (t/todos-by-todo-list db todo-list-id)]))}

   :create {:post!          (fn [ctx]
                              (-> @(d/transact (ed/conn ctx) [(merge {:db/id (d/tempid :db.part/user)}
                                                                     (el/params ctx))])
                                  (el/->ctx :result)))
            :handle-created (fn [ctx] (tl/todo-lists (ed/db-after ctx)))}})
