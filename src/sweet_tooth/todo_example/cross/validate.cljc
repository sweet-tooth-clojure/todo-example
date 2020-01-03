(ns sweet-tooth.todo-example.cross.validate
  (:require [sweet-tooth.describe :as d]))

(def todo-list-title-empty
  (d/empty :todo-list/title "todo list title required"))

(def todo-list-rules
  [todo-list-title-empty])

(def todo-title-empty
  (d/empty :todo/title "todo list title required"))

(def todo-rules
  [todo-title-empty])
