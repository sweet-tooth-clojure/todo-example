(ns sweet-tooth.todo-example.frontend.routes
  (:require [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.todo-example.frontend.components.todo-lists.list :as tll]
            [sweet-tooth.todo-example.frontend.components.todo-lists.show :as tls]
            [clojure.spec.alpha :as s]
            [reitit.coercion.spec :as rs]))

(s/def :db/id int?)

(def frontend-routes
  [["/"
    {:name       :home
     :lifecycle  {:param-change [::stsf/sync-once [:get :todo-lists]]}
     :components {:side [tll/component]
                  :main [tls/component]}
     :title      "Todo List"}]

   ["/todo-list/{db/id}"
    {:name       :show-todo-list
     :lifecycle  {:param-change (fn [_ {:keys [params]}]
                                  [[::stsf/sync-once [:get :todo-lists]]
                                   [::stsf/sync [:get :todo-list {:params params}]]])}
     :components {:side [tll/component]
                  :main [tls/component]}
     :coercion   rs/coercion
     :parameters {:path (s/keys :req [:db/id])}
     :title      "Todo List"}]])
