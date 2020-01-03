(ns sweet-tooth.todo-example.backend.db.validate
  (:require [sweet-tooth.describe :as d]
            [sweet-tooth.endpoint.liberator :as el]))

(def todo-list-title-empty
  (d/empty :todo-list/title "todo list title required"))

(def todo-list-rules
  [todo-list-title-empty])

(def todo-title-empty
  (d/empty :todo/title "todo list title required"))

(def todo-rules
  [todo-title-empty])

(defn validate-describe
  [rules & [describe-context]]
  (fn [ctx]
    (when-let [descriptions (d/describe (el/params ctx)
                                        rules
                                        (when describe-context (describe-context ctx)))]
      [true (el/errors-map (d/map-rollup-descriptions descriptions))])))
