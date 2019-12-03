(ns sweet-tooth.todo-example.frontend.routes
  (:require [sweet-tooth.todo-example.frontend.components.todo-lists.list :as tll]
            [sweet-tooth.todo-example.frontend.components.todo-lists.show :as tls]

            [clojure.spec.alpha :as s]
            [reitit.coercion.spec :as rs]))

(s/def :db/id int?)

(def frontend-routes
  [["/"
    {:name       :home
     :lifecycle  {:param-change [:load-todo-lists]}
     :components {:main [tll/component]}
     :title      "Todo List"}]

   ["/todo-list/{db/id}"
    {:name       :show-todo-list
     :lifecycle  {:param-change (fn [_ {:keys [params]}]
                                  [:load-todo-list params])}
     :components {:main [tls/component]}
     :coercion   rs/coercion
     :parameters {:path (s/keys :req [:db/id])}
     :title      "Todo List"}]])
