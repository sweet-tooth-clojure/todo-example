(ns sweet-tooth.todo-example.frontend.components.todo-lists.show
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.todo-example.cross.utils :as u]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]))

(defn todo
  [t]
  (let [path [:todo :update (select-keys t [:db/id])]
        this (r/current-component)]
    (stfc/with-form path
      (if @form-ui-state
        [:li.todo
         {:on-click #(let [this-dom-node (r/dom-node this)
                           target (u/go-get % "target")]
                       (when (or (= this-dom-node target)
                                 (.contains this-dom-node target))
                         (.stopImmediatePropagation (u/go-get % "nativeEvent"))))}
         [:form
          (merge (on-submit {:data  (select-keys t [:todo/todo-list])
                             :clear :all}))
          [(ui/focus-child [input :text :todo/title] "input")]]
         [:span {:on-click #(rf/dispatch [:close-todo-form path t])} "cancel"]
         [:span {:on-click #(rf/dispatch [:delete-todo t])} "delete"]]
        [:li.todo
         {:on-click #(rf/dispatch [:open-todo-form path t])}
         (:todo/title t)]))))

(defn todo-list
  [tl]
  (let [todos @(rf/subscribe [:todos])]
    [:div [:h2 (:todo-list/title tl)]
     (stfc/with-form [:todos :create]
       [:form (on-submit {:clear :all
                          :data {:todo/todo-list (:db/id tl)}})
        [field :text :todo/title {:label "New Todo"}]
        [:input {:type "submit"}]])
     [:ul (doall (map (fn [t] ^{:key (:db/id t)} [todo t])
                      todos))]]))

(defn component
  []
  (if-let [tl @(rf/subscribe [:routed-todo-list])]
    [todo-list tl]
    [:div "Select a todo list"]))
