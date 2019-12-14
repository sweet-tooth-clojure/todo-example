(ns sweet-tooth.todo-example.frontend.components.todo-lists.show
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.todo-example.cross.utils :as u]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]))

(defn stop-clicks
  []
  (let [this (r/current-component)]
    #(let [this-dom-node (r/dom-node this)
           target        (u/go-get % "target")]
       (when (or (= this-dom-node target)
                 (.contains this-dom-node target))
         (.stopImmediatePropagation (u/go-get % "nativeEvent"))))))

(defn todo
  [t]
  (let [path [:todo :update (select-keys t [:db/id])]]
    (stfc/with-form path
      (if @form-ui-state
        [:li.todo
         {:on-click (stop-clicks)}
         [:form {:on-submit (u/prevent-default #(rf/dispatch [:submit-form path t]))}
          [(ui/focus-child [input :text :todo/title])]]
         [:span {:on-click #(rf/dispatch [:close-form path t])}
          [:i.fas.fa-window-close]]
         " "
         [:span {:on-click #(do (rf/dispatch [:delete-todo t])
                                (rf/dispatch [:close-form path t]))}
          [:i.fas.fa-trash]]]
        [:li.todo
         {:on-click #(rf/dispatch [:open-form path t])}
         (:todo/title t)]))))

(defn todo-list-title
  [tl]
  (let [path [:todo-list :update (select-keys tl [:db/id])]]
    (stfc/with-form path
      [:h2 (if @form-ui-state
             [:div {:on-click (stop-clicks)}
              [:form {:on-submit (u/prevent-default #(rf/dispatch [:submit-form path tl]))}
               [(ui/focus-child [input :text :todo-list/title])]]
              [:span {:on-click #(rf/dispatch [:close-form path tl])}
               [:i.fas.fa-window-close]]
              " "
              [:span {:on-click #(do (rf/dispatch [:delete-todo-list tl])
                                     (rf/dispatch [:close-form path tl]))}
               [:i.fas.fa-trash]]]
             [:div
              {:on-click #(rf/dispatch [:open-form path tl])}
              (:todo-list/title tl)])])))

(defn todo-list
  [tl]
  (let [todos @(rf/subscribe [:todos])]
    [:div.todo-list
     [todo-list-title tl]
     (stfc/with-form [:todos :create]
       [:form.new-todo (on-submit {:clear :all
                                   :data  {:todo/todo-list (:db/id tl)}
                                   :sync  {:on {:success [[::stff/submit-form-success :$ctx]
                                                          [:focus-element "#todo-title" 100]]}}})
        [input :text :todo/title {:placeholder "New Todo" :id "todo-title"}]
        [:input {:type "submit"}]])
     (if (empty? todos)
       [:div "No todos yet"]
       [:ol.todos (doall (map (fn [t] ^{:key (:db/id t)} [todo t])
                              todos))])]))

(defn component
  []
  (if-let [tl @(rf/subscribe [:routed-todo-list])]
    [todo-list tl]
    [:div "Select a todo list to view its todos"]))
