(ns sweet-tooth.todo-example.backend.endpoint.todo-list
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]
            [sweet-tooth.todo-example.backend.db.validate :as v]))

(def decisions
  {:collection
   {:get  {:handle-ok (comp tl/todo-lists ed/db)}
    :post {:malformed?     (v/validate-describe v/todo-list-rules)
           :post!          ed/create->:result
           :handle-created ed/created-pull}}

   :member
   {;; TODO break this up, use exists? decision
    :get {:handle-ok (fn [ctx]
                       (when-let [todo-list (ed/pull-ctx-id ctx)]
                         [todo-list (t/todos-by-todo-list (ed/db ctx) (:db/id todo-list))]))}


    :put {:put!      ed/update->:result
          :handle-ok ed/updated-pull}

    :delete {:delete!   (fn [ctx]
                          (let [tl-id (ed/ctx-id ctx)
                                todos (t/todos-by-todo-list (ed/db ctx) tl-id)]
                            @(d/transact (ed/conn ctx) (->> todos
                                                            (map :db/id)
                                                            (into [tl-id])
                                                            (map (fn [id] [:db.fn/retractEntity id]))))))
             :handle-ok []}}})
