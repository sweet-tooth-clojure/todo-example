(ns sweet-tooth.todo-example.frontend.components.todo-lists.list
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.routes :as stfr]))

(defn component
  []
  (let [todo-lists @(rf/subscribe [:todo-lists])]
    [:div
     [:div (count todo-lists) " Todo lists"]

     (stfc/with-form [:todo-lists :create]
       [:form (on-submit)
        [field :text :todo-list/title]
        [:input {:type "submit"}]])

     (->> todo-lists
          (map (fn [tl]
                 ^{:key (:db/id tl)}
                 [:div [:a {:href (stfr/path :show-todo-list tl)} (:todo-list/title tl)]]))
          doall)]))
