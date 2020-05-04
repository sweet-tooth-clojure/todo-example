(ns sweet-tooth.todo-example.frontend.components.app
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.frontend.nav.flow :as stnf]))

(defn app
  []
  [:div.app
   [:div.head
    [:div.container [:a {:href (stfr/path :home)} "Wow! A To-Do List!"]]]
   [:div.container.grid
    [:div.side @(rf/subscribe [::stnf/routed-component :side])]
    [:div.main @(rf/subscribe [::stnf/routed-component :main])]]])
