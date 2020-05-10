(ns sweet-tooth.todo-example.frontend.handlers
  (:require [goog.events]
            [re-frame.core :as rf]

            [sweet-tooth.frontend.core.flow :as stcf]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.frontend.paths :as paths]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.frontend.js-event-handlers.flow :as stjehf])
  (:import [goog.events EventType]))

(defn- remove-entity-from-db
  [db ent-type ent]
  (update-in db [:entity ent-type] dissoc (:db/id ent)))

(defn- delete-entity-optimistic
  [ent-type]
  (fn [{:keys [db]} args]
    {:dispatch [::stsf/sync-entity (into [:delete :todo-list] args)]
     :db (remove-entity-from-db db ent-type (first args))}))

;;------
;; todo lists
;;------

(rf/reg-event-fx :delete-todo-list
  [rf/trim-v]
  (fn [{:keys [db]} [todo-list]]
    {:dispatch-n [[::stsf/sync-entity [:delete :todo-list todo-list]]
                  [::stnf/navigate-route :home]]
     :db (remove-entity-from-db db :todo-list todo-list)}))

(rf/reg-event-fx :select-created-todo-list
  [rf/trim-v]
  (fn [_cofx [args]]
    {:dispatch [::stnf/navigate-route :show-todo-list (stsf/single-entity args)]}))

;;------
;; todos
;;------

(rf/reg-event-fx :toggle-todo
  [rf/trim-v]
  (fn [{:keys [db]} [todo]]
    (let [new-todo (-> todo
                       (select-keys [:db/id :todo/done?])
                       (update :todo/done? not))]
      {:dispatch [::stsf/sync-entity [:put :todo new-todo]]
       :db       (stcf/merge-entity db :todo :db/id new-todo)})))

(rf/reg-event-fx :delete-todo
  [rf/trim-v]
  (delete-entity-optimistic :todo))

;;------
;; inline forms
;;------

;; clicks outside of the form will submit the form
(rf/reg-event-fx :open-form
  [rf/trim-v]
  (fn [{:keys [db]} [path ent]]
    {:db               (stff/toggle-form db path ent)
     ::stjehf/register [[:close-and-submit-form (:db/id ent)]
                        [[js/window
                          EventType.CLICK
                          (fn [] (rf/dispatch [:close-and-submit-form path ent]))]]]}))

(defn close-form
  [{:keys [db]} [path ent]]
  {:db                 (assoc-in db (paths/full-path :form path :ui-state) nil)
   ::stjehf/unregister [:close-and-submit-form (:db/id ent)]})

(rf/reg-event-fx :close-and-submit-form
  [rf/trim-v]
  (fn [{:keys [db] :as ctx} [path ent :as args]]
    (let [form (paths/get-path db :form path)]
      (cond-> (close-form ctx args)
        (and (:ui-state form) (not= (:base form) (:buffer form)))
        (assoc :dispatch [::stff/submit-form path {:sync {:on {:success [::stff/submit-form-success :$ctx {:clear [:buffer]}]}}}])))))

(rf/reg-event-fx :close-form
  [rf/trim-v]
  close-form)

;;------
;; ui
;;------

(rf/reg-event-fx :focus-element
  [rf/trim-v]
  (fn [_ args]
    {:focus-element args}))

(rf/reg-fx :focus-element
  ;; TODO update this to wait for the element to be ready, then select it
  (fn [[selector timeout]]
    (js/setTimeout #(.focus (js/document.querySelector selector)) timeout)))
