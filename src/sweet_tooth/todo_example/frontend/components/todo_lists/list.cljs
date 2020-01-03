(ns sweet-tooth.todo-example.frontend.components.todo-lists.list
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]))

(defn component
  []
  (let [todo-lists         @(rf/subscribe [:todo-lists])
        current-todo-list@ (rf/subscribe [:routed-todo-list])]
    [:div.todo-lists
     (stfc/with-form [:todo-lists :create]
       [:form (on-submit {:clear  [:buffer :ui-state]
                          :expire {:state 3000}
                          :sync   {:on {:success [[::stff/submit-form-success :$ctx]
                                                  [:select-created-todo-list :$ctx]
                                                  [:focus-element "#todo-list-title" 100]]}}})
        [field :text :todo-list/title
         {:id          "todo-list-title"
          :placeholder "New Todo List"
          :no-label    true}]
        [:input {:type "submit"}]
        [ui/form-state-feedback form]])

     [ui/loadable-component
      [::stsf/sync-state [:get :todo-lists]]
      "no todo lists"
      [:div
       [:h3 (count todo-lists) " Todo lists"]
       (->> todo-lists
            (map (fn [tl]
                   ^{:key (:db/id tl)}
                   [:div.todo-list
                    [:a {:class (when (= current-todo-list tl) "active")
                         :href  (stfr/path :show-todo-list tl)}
                     (:todo-list/title tl)]]))
            doall)]]]))
