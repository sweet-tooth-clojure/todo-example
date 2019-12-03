(ns sweet-tooth.todo-example.frontend.components.todo-lists.list
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]))

(defn component
  []
  (let [todo-lists @(rf/subscribe [:todo-lists])]
    [:div
     [:div (count todo-lists) " Todo lists"]
     (stfc/with-form [:todo-lists :create]
       [:form (on-submit)
        [field :text :todo-list/title]
        [:input {:type "submit"}]])]))
