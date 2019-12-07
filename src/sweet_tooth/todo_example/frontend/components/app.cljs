(ns sweet-tooth.todo-example.frontend.components.app
  (:require [reagent.core :as r]
            [re-frame.core :as rf]

            [sweet-tooth.frontend.nav.flow :as stnf]))

(defn app
  []
  [:div.app
   @(rf/subscribe [::stnf/routed-component :side])
   @(rf/subscribe [::stnf/routed-component :main])])
