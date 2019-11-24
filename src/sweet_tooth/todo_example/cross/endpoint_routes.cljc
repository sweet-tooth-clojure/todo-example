(ns sweet-tooth.todo-example.cross.endpoint-routes
  (:require [sweet-tooth.endpoint.routes.reitit :as serr]
            [integrant.core :as ig]))

(def ns-routes
  (serr/ns-pairs->ns-routes
    [{:ctx         {:db (ig/ref :sweet-tooth.endpoint.datomic/connection)}
      :id-key      :db/id
      :auth-id-key :db/id
      :path-prefix "/api/v1"}

     [:sweet-tooth.todo-example.backend.endpoint.todo-list]

     {:handler (ig/ref :sweet-tooth.todo-example.backend.endpoint.static/handler)}
     ["/"]]))

(defmethod ig/init-key ::routes [_ _]
  ns-routes)
