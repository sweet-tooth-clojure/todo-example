(ns sweet-tooth.todo-example.cross.endpoint-routes
  (:require [sweet-tooth.endpoint.routes.reitit :as serr]
            [integrant.core :as ig]))

(def routes
  (serr/expand-routes
   [{:ctx               {:db (ig/ref :sweet-tooth.endpoint.datomic/connection)}
     :id-key            :db/id
     :auth-id-key       :db/id
     ::serr/path-prefix "/api/v1"}
    [:sweet-tooth.todo-example.backend.endpoint.todo-list]
    [:sweet-tooth.todo-example.backend.endpoint.todo]]))

(defmethod ig/init-key ::routes [_ _]
  routes)
