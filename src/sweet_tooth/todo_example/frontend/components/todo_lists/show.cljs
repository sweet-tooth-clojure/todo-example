(ns sweet-tooth.todo-example.frontend.components.todo-lists.show
  (:require [re-frame.core :as rf]))

(defn component
  []
  (let [todo-list @(rf/subscribe [:routed-todo-list])]
    [:div "TODO LIST:" (str todo-list)]))
