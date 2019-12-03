(ns sweet-tooth.todo-example.frontend.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :todo-lists
  (fn [db]
    (->> (get-in db [:entity :todo-list])
         vals
         (sort-by :todo-list/title))))
