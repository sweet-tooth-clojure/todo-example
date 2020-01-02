(ns sweet-tooth.todo-example.backend.endpoint.todo
  (:require [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.todo-example.backend.db.query.todo :as t]))

(defn result-todos
  [ctx]
  (t/todos-by-todo-list (ed/db-after ctx) (:todo/todo-list (el/params ctx))))

(def decisions
  {:create {:post!          ed/create->:result
            :handle-created ed/created-pull}

   :update {:put!      ed/update->:result
            :handle-ok ed/updated-pull}

   :delete {:delete!   ed/delete->:result
            :handle-ok []}})
