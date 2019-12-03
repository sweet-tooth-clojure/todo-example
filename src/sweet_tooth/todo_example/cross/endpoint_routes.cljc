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

(defmethod ig/init-key ::routes [_ prefix]
  ;; Adding a prefix allows the frontend to e.g. specify a different
  ;; port number for requests when the prefix is `http://localhost:3010`
  (cond->> ns-routes
    prefix (mapv (fn [route] (update route 1 #(assoc % :prefix prefix))))))
