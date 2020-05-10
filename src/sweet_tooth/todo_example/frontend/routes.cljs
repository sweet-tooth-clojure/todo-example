(ns sweet-tooth.todo-example.frontend.routes
  (:require [sweet-tooth.frontend.sync.flow :as stsf]
            [sweet-tooth.frontend.form.flow :as stff]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.todo-example.cross.validate :as v]
            [sweet-tooth.todo-example.frontend.components.home :as h]
            [sweet-tooth.todo-example.frontend.components.todo-lists.list :as tll]
            [sweet-tooth.todo-example.frontend.components.todo-lists.show :as tls]
            [sweet-tooth.todo-example.frontend.components.ui :as ui]
            [clojure.spec.alpha :as s]
            [reitit.coercion.spec :as rs]))

(s/def :db/id int?)

(def frontend-routes
  [["/"
    {:name       :home
     :lifecycle  {:param-change [[::stsf/sync-once [:get :todo-lists]]
                                 [::stff/initialize-form [:home-new-todo-list :create]]]}
     :components {:side [tll/component]
                  :main [h/component]}
     :title      "Todo List"}]

   ["/todo-list/{db/id}"
    {:name       :show-todo-list
     :lifecycle  {:param-change [[::stff/initialize-form [:todos :create] {:validate (ui/validate-with v/todo-rules)}]
                                 [::stsf/sync-once [:get :todo-lists]]
                                 [::stnf/get-with-route-params :todo-list]]}
     :components {:side [tll/component]
                  :main [tls/component]}
     :coercion   rs/coercion
     :parameters {:path (s/keys :req [:db/id])}
     :title      "Todo List"}]])
