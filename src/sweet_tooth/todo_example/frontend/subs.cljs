(ns sweet-tooth.todo-example.frontend.subs
  (:require [re-frame.core :as rf]
            [medley.core :as medley]
            [sweet-tooth.describe :as d]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.nav.utils :as stnu]
            [sweet-tooth.todo-example.cross.utils :as u]
            [sweet-tooth.todo-example.cross.validate :as v]))

(rf/reg-sub :todo-lists
  (fn [db]
    (->> (get-in db [:entity :todo-list])
         vals
         (sort-by :todo-list/title))))

(defn routed-todo-list
  [db]
  (stnu/routed-entity db :todo-list :db/id))

(rf/reg-sub :routed-todo-list routed-todo-list)

(rf/reg-sub :todos
  (fn [db]
    (let [tl (routed-todo-list db)]
      (->> (get-in db [:entity :todo])
           vals
           (filter #(= (get-in % [:todo/todo-list :db/id]) (:db/id tl)))))))

;; show errors on submit

(rf/reg-sub :todo-validation
  (fn [[_ partial-form-path]]
    (rf/subscribe [::stff/form partial-form-path]))
  (fn [{:keys [buffer input-events]} [_ _ attr-path]]
    (let [errors (->> (d/describe buffer v/todo-rules)
                      (d/map-rollup-descriptions)
                      (medley/map-vals (fn [d] {:errors d})))]
      (if attr-path
        (when (or (contains? (::stff/form input-events) "submit")
                  (contains? (::stff/form input-events) "submit-click"))
          (get-in errors (u/flatv attr-path)))
        (cond->> {:prevent-submit? (seq errors)})))))
