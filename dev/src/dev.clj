(ns dev
  "Consolidate often-used function calls for easier REPLing"
  (:refer-clojure :exclude [test])
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.tools.namespace.repl]
            [duct.core :as duct]
            [duct.core.repl :as duct-repl]

            [datomic.api :as d]

            [sweet-tooth.todo-example.backend.duct :as app-duct] ;; for multimethod definitions
            [sweet-tooth.todo-example.backend.db.tasks :as dbt]

            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init reset]]
            [integrant.repl.state :refer [config system]]

            [sweet-tooth.endpoint.system :as es]
            [sweet-tooth.endpoint.test.harness :as eth]
            [com.flyingmachine.datomic-junk :as dj]))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(defn read-config []
  (duct/read-config (io/resource "config.edn")))

(defn prep []
  (es/config :dev))

(integrant.repl/set-prep! prep)

(defn db
  []
  (d/db (d/connect (:uri (:sweet-tooth.endpoint.datomic/connection system)))))
