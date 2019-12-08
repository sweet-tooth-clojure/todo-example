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

(rf/reg-event-fx :open-todo-form
  [rf/trim-v]
  (fn [{:keys [db]} [todo-path todo]]
    {:db               (stff/toggle-form db todo-path todo)
     ::stjehf/register [[:submit-todo-form (:db/id todo)]
                        [[js/window
                          EventType.CLICK
                          (fn [] (rf/dispatch [:submit-todo-form todo-path todo]))]]]}))

(defn close-todo-form
  [{:keys [db]} [todo-path todo]]
  {:db                 (assoc-in db (paths/full-path :form todo-path :ui-state) nil)
   ::stjehf/unregister [:submit-todo-form (:db/id todo)]})

(rf/reg-event-fx :submit-todo-form
  [rf/trim-v]
  (fn [{:keys [db] :as ctx} [todo-path todo :as args]]
    (cond-> (close-todo-form ctx args)
      (paths/get-path db :form todo-path :ui-state)
      (assoc :dispatch [::stff/submit-form todo-path {:clear :all
                                                      :data  (select-keys todo [:todo/todo-list])}]))))

(rf/reg-event-fx :close-todo-form
  [rf/trim-v]
  close-todo-form)
