(ns sweet-tooth.todo-example.frontend.subs
  (:require [re-frame.core :as rf]
            [sweet-tooth.frontend.nav.utils :as stnu]))

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
           (filter #(= (:todo/todo-list %) (:db/id tl)))))))
