(ns sweet-tooth.todo-example.frontend.routes
  (:require [sweet-tooth.todo-example.frontend.components.todo-lists.list :as tll]))

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
     :components {:main [tll/component]}
     :title      "Todo List"}]])
