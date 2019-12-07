(ns sweet-tooth.todo-example.frontend.handlers
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.sync.flow :as stsf]))

(rf/reg-event-fx :load-todo-lists
  [rf/trim-v]
  (stsf/sync-fx [:get :todo-lists]))

(rf/reg-event-fx :load-todo-list
  [rf/trim-v]
  (stsf/sync-fx [:get :todo-list]))

(rf/reg-event-fx :delete-todo
  [rf/trim-v]
  (fn [{:keys [db] :as cofx} args]
    (merge ((stsf/sync-fx [:delete :todo]) cofx args)
           {:db (update-in db [:entity :todo] dissoc (:db/id (first args)))})))
