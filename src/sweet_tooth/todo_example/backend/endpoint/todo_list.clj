(ns sweet-tooth.todo-example.backend.endpoint.todo-list
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.utils :as eu]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.todo-example.backend.db.query.todo-list :as tl]))

(def decisions
  {:list {:handle-ok (fn [ctx] (tl/todo-lists (ed/db ctx)))}})
