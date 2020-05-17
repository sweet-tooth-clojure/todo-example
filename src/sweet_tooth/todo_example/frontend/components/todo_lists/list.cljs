(ns sweet-tooth.todo-example.frontend.components.todo-lists.list
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]))

(defn component
  []
  (let [todo-lists        @(rf/subscribe [:todo-lists])
        current-todo-list @(rf/subscribe [:routed-todo-list])]
    [:div.todo-lists
     (stfc/with-form [:sidebar-new-todo-list :create]
       [:form (on-submit {:sync {:route-name :todo-lists
                                 :on         {:success [[::stff/submit-form-success :$ctx {:clear [:buffer :ui-state]}]
                                                        [::stnf/navigate-to-synced-entity :show-todo-list :$ctx]
                                                        [:focus-element "#todo-list-title" 100]]}}})
        [field :text :todo-list/title
         {:id          "todo-list-title"
          :placeholder "new to-do list title"
          :no-label    true}]
        [:input {:type "submit" :value "create to-do list"}]
        [ui/form-state-feedback form]])

     [ui/loadable-component
      [::stsf/sync-state [:get :todo-lists]]
      [:em "no to-do lists"]
      [:div
       [:h3 (count todo-lists) " To-do lists"]
       (->> todo-lists
            (map (fn [tl]
                   ^{:key (:db/id tl)}
                   [:div.todo-list
                    [:a {:class (when (= current-todo-list tl) "active")
                         :href  (stfr/path :show-todo-list tl)}
                     (:todo-list/title tl)]]))
            doall)]]]))
