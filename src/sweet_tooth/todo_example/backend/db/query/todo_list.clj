(ns sweet-tooth.todo-example.backend.db.query.todo-list
  (:require [datomic.api :as d]
            [com.flyingmachine.datomic-junk :as dj]
            [sweet-tooth.endpoint.utils :as eu]))

(defn todo-lists
  [db]
  (d/q '[:find (pull ?e [:db/id :todo-list/title])
         :in $]
       db))
