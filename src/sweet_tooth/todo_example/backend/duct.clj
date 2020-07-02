(ns sweet-tooth.todo-example.backend.duct
  (:require [clojure.java.io :as io]
            [duct.core :as duct]
            [sweet-tooth.endpoint.system :as es]))

(duct/load-hierarchy)

;;--------
;; Configs
;;--------
(defn read-config []
  (duct/read-config (io/resource "config.edn")))

(defn prep [& [profiles]]
  (duct/prep-config (read-config) profiles))

(defmethod es/config :test
  [_]
  (dissoc (prep [:duct.profile/test]) :duct.server.http/jetty))

(defmethod es/config :dev
  [_]
  (prep [:duct.profile/dev :duct.profile/local]))

(defmethod es/config :prod
  [_]
  (prep [:duct.profile/prod]))
