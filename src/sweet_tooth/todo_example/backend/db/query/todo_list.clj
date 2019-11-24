(ns sweet-tooth.todo-example.backend.db.query.todo-list
  (:require [datomic.api :as d]))

(defn todo-lists
  [db]
  (d/q '[:find (pull ?e [:db/id :todo-list/title])
         :where [?e :todo-list/title]
         :in $]
       db))
