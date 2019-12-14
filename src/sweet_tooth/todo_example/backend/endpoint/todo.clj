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
  {:create {:post!          ed/create->:result
            :handle-created result-todos}

   :update {:put!      ed/update->:result
            :handle-ok result-todos}

   :delete {:delete!   ed/delete->:result
            :handle-ok []}})
