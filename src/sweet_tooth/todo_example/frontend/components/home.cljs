(ns sweet-tooth.todo-example.frontend.components.home
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.form.components :as stfc]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]
            [sweet-tooth.todo-example.frontend.components.ui.simplemde]))


(defn component
  []
  (let [todo-lists @(rf/subscribe [:todo-lists])]
    [:div.home
     (if (empty? todo-lists)
       [:div.new-todo-list
        [:h1 "Welcome!"]
        [:div "To get started, create a new to-do list:"]
        (stfc/with-form [:todo-lists :create]
          [:form (on-submit {:sync {:on {:success [[::stff/clear form-path #{:buffer :ui-state}]
                                                   [::stnf/navigate-to-synced-entity :show-todo-list :$ctx]
                                                   [:focus-element "#todo-list-title" 100]]}}})
           [input :text :todo-list/title
            {:placeholder "new to-do list title"
             :no-label    true}]
           [:input {:type "submit" :value "create to-do list"}]
           [ui/form-state-feedback form]])]
       [:div.todo-list-summary
        [:h1 "Welcome!"]
        [:div "Select a to-do list:"]
        (->> todo-lists
             (map (fn [tl]
                    ^{:key (:db/id tl)}
                    [:div.todo-list
                     [:a {:href (stfr/path :show-todo-list tl)}
                      (:todo-list/title tl)]]))
             doall)])]))
