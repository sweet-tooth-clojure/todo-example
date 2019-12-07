(ns sweet-tooth.todo-example.frontend.components.todo-lists.show
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]))

(defn todo-list
  [tl]
  (let [todos @(rf/subscribe [:todos])]
    [:div [:h2 (:todo-list/title tl)]
     (stfc/with-form [:todos :create]
       [:form (on-submit {:data {:todo/todo-list (:db/id tl)}})
        [field :text :todo/title {:label "New Todo"}]
        [:input {:type "submit"}]])
     [:ul (doall (map (fn [t]
                        ^{:key (:db/id t)}
                        [:li (:todo/title t)])
                      todos))]]))

(defn component
  []
  (if-let [tl @(rf/subscribe [:routed-todo-list])]
    [todo-list tl]
    [:div "Select a todo list"]))
