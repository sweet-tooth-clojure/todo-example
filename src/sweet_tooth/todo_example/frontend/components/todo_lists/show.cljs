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
                           target        (u/go-get % "target")]
                       (when (or (= this-dom-node target)
                                 (.contains this-dom-node target))
                         (.stopImmediatePropagation (u/go-get % "nativeEvent"))))}
         [:form (on-submit {:data  (select-keys t [:todo/todo-list])
                            :clear :all})
          [(ui/focus-child [input :text :todo/title])]]
         [:span {:on-click #(rf/dispatch [:close-form path t])} "cancel"]
         [:span {:on-click #(do (rf/dispatch [:delete-todo t])
                                (rf/dispatch [:close-form path t]))} "delete"]]
        [:li.todo
         {:on-click #(rf/dispatch [:open-form path t])}
         (:todo/title t)]))))

(defn todo-list-title
  [tl]
  (let [path [:todo-list :update (select-keys tl [:db/id])]]
    (stfc/with-form path
      [:h2 (if @form-ui-state
             [:div
              [:form (on-submit {:data  tl
                                 :clear :all})
               [(ui/focus-child [input :text :todo-list/title])]]
              [:span {:on-click #(rf/dispatch [:close-form path tl])} "cancel"]
              [:span {:on-click #(do (rf/dispatch [:delete-todo tl])
                                     (rf/dispatch [:close-form path tl]))} "delete"]]
             [:span
              {:on-click #(rf/dispatch [:open-form path tl])}
              (:todo-list/title tl)])])))

(defn todo-list
  [tl]
  (let [todos @(rf/subscribe [:todos])]
    [:div [todo-list-title tl]
     [:span {:on-click #(rf/dispatch [:delete-todo-list tl])} "delete"]
     (stfc/with-form [:todos :create]
       [:form (on-submit {:clear :all
                          :data  {:todo/todo-list (:db/id tl)}
                          :sync  {:on {:success [[::stff/submit-form-success :$ctx]
                                                 [:focus-element "#todo-title" 100]]}}})
        [field :text :todo/title {:label "New Todo" :id "todo-title"}]
        [:input {:type "submit"}]])
     [:ul (doall (map (fn [t] ^{:key (:db/id t)} [todo t])
                      todos))]]))

(defn component
  []
  (if-let [tl @(rf/subscribe [:routed-todo-list])]
    [todo-list tl]
    [:div "Select a todo list"]))
