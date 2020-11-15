(ns sweet-tooth.todo-example.frontend.components.todo-lists.show
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.todo-example.cross.utils :as u]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]))

(defn- stop-clicks
  "Prevent click propagation. For cases where we've registered a window
  event to close and submit a form, and we don't want clicks on the
  form or its children to trigger the form close event."
  []
  (let [this (r/current-component)]
    #(let [this-dom-node (rdom/dom-node this)
           target        (u/go-get % "target")]
       (when (or (= this-dom-node target)
                 (.contains this-dom-node target))
         (.stopImmediatePropagation (u/go-get % "nativeEvent"))))))

(defn todo-checkbox
  [todo]
  [:span.todo-checkbox
   {:on-click #(do (.stopPropagation %)
                   (rf/dispatch [:toggle-todo todo]))}
   (if (:todo/done? todo)
     [:i.far.fa-check-square]
     [:i.far.fa-square])])

(defn todo
  [t]
  (let [path [:todo :update (select-keys t [:db/id])]]
    (stfc/with-form path
      (if @form-ui-state
        [:li.todo
         {:on-click (stop-clicks)}
         [:form {:on-submit (u/prevent-default #(rf/dispatch [:close-and-submit-form path t]))}
          [(ui/focus-child [input :text :todo/title])]]
         [:span {:on-click #(rf/dispatch [:close-form path t])}
          [:i.fas.fa-window-close]]
         " "
         [:span {:on-click #(do (rf/dispatch [:delete-todo t])
                                (rf/dispatch [:close-form path t]))}
          [:i.fas.fa-trash]]]
        [:li.todo
         {:class    (when (:todo/done? t) "done")
          :on-click #(rf/dispatch [:open-form path t])}
         [todo-checkbox t]
         (:todo/title t)
         [ui/form-state-feedback form]]))))

(defn todo-list-title
  [tl]
  (let [path [:todo-list :update (select-keys tl [:db/id])]]
    (stfc/with-form path
      [:h2 (if @form-ui-state
             [:div {:on-click (stop-clicks)}
              [:form {:on-submit (u/prevent-default #(rf/dispatch [:close-and-submit-form path tl]))}
               [(ui/focus-child [input :text :todo-list/title])]]
              [:span {:on-click #(rf/dispatch [:close-form path tl])}
               [:i.fas.fa-window-close]]
              " "
              [:span {:on-click #(do (rf/dispatch [:delete-todo-list tl])
                                     (rf/dispatch [:close-form path tl]))}
               [:i.fas.fa-trash]]]
             [:div
              {:on-click #(rf/dispatch [:open-form path tl])}
              (:todo-list/title tl)
              [ui/form-state-feedback form]])])))

(defn submit-btn
  [partial-form-path form-dscr]
  [:input {:type     "submit"
           :value    "create to-do"
           :on-click #(rf/dispatch [::stff/form-input-event {:partial-form-path partial-form-path
                                                             :event-type        "submit-click"}])}])

(defn component
  []
  (let [route @(rf/subscribe [::stnf/route])
        tl    @(rf/subscribe [:routed-todo-list])
        todos @(rf/subscribe [:todos])]
    (if (= :home (:route-name route))
      [:div "Select a to-do list to view its to-dos"]
      [ui/loadable-component
       [::stsf/sync-state [:get :todo-list (:params route)]]
       "Could not find that to-do list"
       (when tl
         [:div.todo-list
          [todo-list-title tl]
          (stfc/with-form [:todos :create]
            {:dscr-sub :todo-validation}
            [:form.new-todo
             {:on-submit
              (fn [e]
                (if-not (:prevent-submit? @form-dscr)
                  ((on-submit-handler
                    {:data {:todo/todo-list (:db/id tl)}
                     :sync {:on {:success [[::stff/clear form-path #{:buffer :ui-state :input-events}]
                                           [:focus-element "#todo-title" 100]]}}})
                   e)
                  (.preventDefault e)))}

             [field :text :todo/title {:placeholder "new to-do"
                                       :id          "todo-title"
                                       :no-label    true}]
             [submit-btn form-path form-dscr]
             [ui/form-state-feedback form]])
          (if (seq todos)
            [:ol.todos (doall (map (fn [t] ^{:key (:db/id t)} [todo t])
                                   todos))]
            [:em "No to-dos yet"])])])))
