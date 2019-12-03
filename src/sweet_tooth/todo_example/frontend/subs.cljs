(ns sweet-tooth.todo-example.frontend.subs
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.nav.utils :as stnu]))

(rf/reg-sub :todo-lists
  (fn [db]
    (->> (get-in db [:entity :todo-list])
         vals
         (sort-by :todo-list/title))))

(rf/reg-sub :routed-todo-list
  (fn [db]
    (let [x (stnu/routed-entity db :todo-list :db/id)]
      (prn "ENT" x)
      x)))
