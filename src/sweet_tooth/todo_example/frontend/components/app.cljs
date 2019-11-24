(ns sweet-tooth.todo-example.frontend.components.app
  (:require [reagent.core :as r]
            [re-frame.core :as rf]

            [sweet-tooth.frontend.nav.flow :as stnf]))

(defn main-component
  []
  @(rf/subscribe [::stnf/routed-component :main]))

(defn app
  []
  [:div
   [:div "App"]
   [main-component]])
