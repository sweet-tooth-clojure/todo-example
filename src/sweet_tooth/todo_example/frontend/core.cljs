(ns sweet-tooth.todo-example.frontend.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-frame.db :as rfdb]
            [integrant.core :as ig]
            [meta-merge.core :as mm]

            [sweet-tooth.frontend.core.flow :as stcf]
            [sweet-tooth.frontend.load-all-handler-ns] ;; for side effects
            [sweet-tooth.frontend.core.utils :as stcu]
            [sweet-tooth.frontend.config :as stconfig]
            [sweet-tooth.frontend.nav.flow :as stnf]
            [sweet-tooth.frontend.routes :as stfr]
            [sweet-tooth.frontend.js-event-handlers.flow :as stjehf]

            [sweet-tooth.todo-example.frontend.components.app :as app]
            [sweet-tooth.todo-example.frontend.handlers] ;; for side effects
            [sweet-tooth.todo-example.frontend.routes :as froutes]
            [sweet-tooth.todo-example.frontend.subs] ;; for side effects
            [sweet-tooth.todo-example.cross.endpoint-routes :as eroutes]))

(enable-console-print!)

;; treat node lists as seqs; not related to the rest
(extend-protocol ISeqable
  js/NodeList
  (-seq [node-list] (array-seq node-list))

  js/HTMLCollection
  (-seq [node-list] (array-seq node-list)))

(defn system-config
  "This is a function instead of a static value so that it will pick up
  reloaded changes"
  []
  (mm/meta-merge stconfig/default-config
                 {::stfr/frontend-router {:use    :reitit
                                          :routes froutes/frontend-routes}
                  ::stfr/sync-router     {:use    :reitit
                                          :routes (ig/ref ::eroutes/routes)}

                  ;; Treat handler registration as an external service,
                  ;; interact with it via re-frame effects
                  ::stjehf/handlers {}
                  ::eroutes/routes  ""}))

(defn -main []
  (rf/dispatch-sync [::stcf/init-system (system-config)])
  (rf/dispatch-sync [::stnf/dispatch-current])
  (r/render [app/app] (stcu/el-by-id "app")))

(defonce initial-load (delay (-main)))
@initial-load

(defn stop [_]
  (when-let [system (:sweet-tooth/system @rfdb/app-db)]
    (ig/halt! system)))
