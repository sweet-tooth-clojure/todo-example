(ns sweet-tooth.todo-example.backend.db.query.todo
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.utils :as eu]))

(defn todos-by-todo-list
  [db todo-list-id]
  (->> (d/q '[:find (pull ?e [:db/id
                              :todo/title
                              :todo/todo-list])
              :where [?e :todo/todo-list ?todo-list-id]
              :in $ ?todo-list-id]
            db todo-list-id)
       (map first)
       (eu/ent-type :todo)))
