(ns sweet-tooth.todo-example.backend.endpoint.todo-list
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]
            [sweet-tooth.todo-example.backend.db.validate :as v]))

(defn fetch-todo-list
  [ctx]
  (let [x (d/pull (ed/db ctx) '[:*] (ed/ctx-id ctx))]
    (when (:todo-list/title x) x)))

(def decisions
  {:list   {:handle-ok (fn [ctx] (tl/todo-lists (ed/db ctx)))}
   :show   {:handle-ok (fn [ctx] (when-let [todo-list (fetch-todo-list ctx)]
                                   [todo-list (t/todos-by-todo-list (ed/db ctx) (:db/id todo-list))]))}
   :create {:malformed?     (v/validate-describe v/todo-list-rules)
            :post!          ed/create->:result
            :handle-created ed/created-pull}

   :update {:put!      ed/update->:result
            :handle-ok ed/updated-pull}

   :delete {:delete!   (fn [ctx]
                         (let [tl-id (ed/ctx-id ctx)
                               todos (t/todos-by-todo-list (ed/db ctx) tl-id)]
                           @(d/transact (ed/conn ctx) (->> todos
                                                           (map (fn [t] [:db.fn/retractEntity (:db/id t)]))
                                                           (into [[:db.fn/retractEntity tl-id]])))))
            :handle-ok []}})
