(ns sweet-tooth.todo-example.backend.endpoint.todo-list
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]
            [sweet-tooth.todo-example.backend.db.validate :as v]))

(def decisions
  {:coll
   {:get  {:handle-ok (comp tl/todo-lists ed/db)}
    :post {:malformed?     (v/validate-describe v/todo-list-rules)
           :post!          ed/create->:result
           :handle-created ed/created-pull}}

   :ent
   {;; TODO break this up, use exists? decision
    :show {:handle-ok (fn [ctx]
                        (when-let [todo-list (ed/pull-ctx-id ctx)]
                          [todo-list (t/todos-by-todo-list (ed/db ctx) (:db/id todo-list))]))}


    :put {:put!      ed/update->:result
          :handle-ok ed/updated-pull}

    :delete {:delete!   (fn [ctx]
                          (let [tl-id (ed/ctx-id ctx)
                                todos (t/todos-by-todo-list (ed/db ctx) tl-id)]
                            @(d/transact (ed/conn ctx) (->> todos
                                                            (into [tl-id])
                                                            (map (fn [t] [:db.fn/retractEntity (:db/id t)]))))))
             :handle-ok []}}})
