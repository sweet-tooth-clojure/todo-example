(ns sweet-tooth.todo-example.backend.endpoint.todo
  (:require [sweet-tooth.endpoint.datomic.liberator :as ed]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.todo-example.cross.validate :as v]))

(def decisions
  {:create {:malformed?     (el/validate-describe v/todo-rules)
            :post!          ed/create->:result
            :handle-created ed/created-pull}

   :update {:put!      ed/update->:result
            :handle-ok ed/updated-pull}

   :delete {:delete!   ed/delete->:result
            :handle-ok []}})
