(ns sweet-tooth.todo-example.frontend.components.app
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.frontend.nav.flow :as stnf]))

(defn app
  []
  [:div.app
   [:div.head
    [:a {:href (stfr/path :home)} "home"]]
   [:div.container
    [:div.side @(rf/subscribe [::stnf/routed-component :side])]
    [:div.main @(rf/subscribe [::stnf/routed-component :main])]]])
