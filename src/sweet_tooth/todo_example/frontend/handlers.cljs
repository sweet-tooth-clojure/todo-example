(ns sweet-tooth.todo-example.frontend.handlers
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.sync.flow :as stsf]))

(rf/reg-event-fx :load-todo-lists
  [rf/trim-v]
  (stsf/sync-fx [:get :todo-lists]))
