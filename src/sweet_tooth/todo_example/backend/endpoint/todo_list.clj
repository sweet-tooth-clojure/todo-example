(ns sweet-tooth.todo-example.backend.endpoint.todo-list
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.utils :as eu]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]))

(defn fetch-todo
  [ctx]
  (d/pull (ed/db ctx) '[:*] (ed/ctx-id ctx)))

(def decisions
  {:list   {:handle-ok (fn [ctx] (tl/todo-lists (ed/db ctx)))}
   :show   {:handle-ok (fn [ctx]
                         (let [todo (fetch-todo ctx)]
                           [todo
                            (t/todos-by-todo-list (ed/db ctx) (:db/id todo))]))}
   :create {:post!          (fn [ctx]
                              (-> @(d/transact (ed/conn ctx) [(merge {:db/id (d/tempid :db.part/user)}
                                                                     (el/params ctx))])
                                  (el/->ctx :result)))
            :handle-created (fn [ctx] (tl/todo-lists (ed/db-after ctx)))}

   :update {:put!      (comp #(el/->ctx % :result) deref ed/update)
            :handle-ok fetch-todo}

   :delete {:delete!   (fn [ctx]
                         (let [tl-id (ed/ctx-id ctx)
                               todos (t/todos-by-todo-list (ed/db ctx) tl-id)]
                           @(d/transact (ed/conn ctx) (->> todos
                                                           (map (fn [t] [:db.fn/retractEntity (:db/id t)]))
                                                           (into [[:db.fn/retractEntity tl-id]])))))
            :handle-ok []}})
