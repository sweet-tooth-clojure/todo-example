(ns sweet-tooth.todo-example.frontend.components.todo-lists.list
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.routes :as stfr]))

(defn component
  []
  (let [todo-lists @(rf/subscribe [:todo-lists])]
    [:div.todo-lists
     (stfc/with-form [:todo-lists :create]
       [:form (on-submit {:clear :all
                          :sync  {:on {:success [[::stff/submit-form-success :$ctx]
                                                 [:select-created-todo-list :$ctx]
                                                 [:focus-element "#todo-list-title" 100]]}}})
        [input :text :todo-list/title {:id "todo-list-title" :placeholder "New Todo List"}]
        [:input {:type "submit"}]])

     [:h3 (count todo-lists) " Todo lists"]

     (->> todo-lists
          (map (fn [tl]
                 ^{:key (:db/id tl)}
                 [:div.todo-list [:a {:href (stfr/path :show-todo-list tl)} (:todo-list/title tl)]]))
          doall)]))
