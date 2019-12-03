(ns sweet-tooth.todo-example.frontend.routes
  (:require [sweet-tooth.todo-example.frontend.components.todo-lists.list :as tll]))

(def frontend-routes
  ["/"
   {:name       :home
    :lifecycle  {:param-change [:load-todo-lists]}
    :components {:main [tll/component]}
    :title      "Todo List"}])
