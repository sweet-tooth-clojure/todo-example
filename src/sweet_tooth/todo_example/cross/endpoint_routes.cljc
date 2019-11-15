(ns sweet-tooth.todo-example.cross.endpoint-routes
  (:require [sweet-tooth.endpoint.routes.reitit :as serr]
            [integrant.core :as ig]))

(def ns-routes
  (serr/ns-pairs->ns-routes
    [#_{:ctx         {:db (ig/ref :sweet-tooth.endpoint.datomic/connection)}
        :id-key      :db/id
        :auth-id-key :db/id
        :path-prefix "/api/v1"}

     #_[:sweet-tooth.todo-example.backend.endpoint.user]

     {:handler (ig/ref :sweet-tooth.todo-example.backend.endpoint.static/handler)}
     ["/"]]))
