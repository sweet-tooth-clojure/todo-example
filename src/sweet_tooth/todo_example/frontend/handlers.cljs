(ns sweet-tooth.todo-example.frontend.handlers
  (:require [goog.events]
            [goog.events.KeyCodes :as KeyCodes]
            [goog.events.KeyHandler :as KeyHandler]
            [goog.events.KeyHandler.EventType :as KeyEventType]

            [re-frame.core :as rf]
            [sweet-tooth.frontend.paths :as paths]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.frontend.js-event-handlers.flow :as stjehf])
  (:import [goog.events EventType]))

(defn delete-and-remove-entity
  [ent-type]
  (fn [{:keys [db] :as cofx} args]
    (merge ((stsf/sync-fx [:delete ent-type]) cofx args)
           {:db (update-in db [:entity ent-type] dissoc (:db/id (first args)))})))

;;------
;; todo lists
;;------

(rf/reg-event-fx :load-todo-lists
  [rf/trim-v]
  (stsf/sync-fx [:get :todo-lists]))

(rf/reg-event-fx :load-todo-list
  [rf/trim-v]
  (stsf/sync-fx [:get :todo-list]))

(rf/reg-event-fx :delete-todo-list
  [rf/trim-v]
  (delete-and-remove-entity :todo-list))

;;------
;; todos
;;------

(rf/reg-event-fx :delete-todo
  [rf/trim-v]
  (delete-and-remove-entity :todo))

;;------
;; inline forms
;;------

(rf/reg-event-fx :open-form
  [rf/trim-v]
  (fn [{:keys [db]} [path ent]]
    {:db               (stff/toggle-form db path ent)
     ::stjehf/register [[:submit-form (:db/id ent)]
                        [[js/window
                          EventType.CLICK
                          (fn [] (rf/dispatch [:submit-form path ent]))]]]}))

(defn close-form
  [{:keys [db]} [path ent]]
  {:db                 (assoc-in db (paths/full-path :form path :ui-state) nil)
   ::stjehf/unregister [:submit-form (:db/id ent)]})

(rf/reg-event-fx :submit-form
  [rf/trim-v]
  (fn [{:keys [db] :as ctx} [path ent :as args]]
    (cond-> (close-form ctx args)
      (paths/get-path db :form path :ui-state)
      (assoc :dispatch [::stff/submit-form path {:clear :all
                                                 :data  ent}]))))

(rf/reg-event-fx :close-form
  [rf/trim-v]
  close-form)
