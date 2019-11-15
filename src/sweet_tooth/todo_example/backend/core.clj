(ns sweet-tooth.todo-example.backend.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.stacktrace :as stacktrace]
            [duct.core :as duct]
            [integrant.core :as ig]
            [sweet-tooth.todo-example.backend.duct]
            [taoensso.timbre :as log]
            [sweet-tooth.endpoint.system :as es]
            [environ.core :as env]
            [datomic.api :as d]))

(duct/load-hierarchy)

(defn init-system
  [env-str profiles]
  (ig/init (es/config (keyword env-str)) profiles))

;; TODO this is probably unnecessary
(defmacro final
  [env & body]
  `(do (try (do ~@body)
            (catch Exception exc#
              (throw exc#)
              (System/exit 1)))
       (System/exit 0)))

(defn start-server
  [env]
  (let [system (init-system env [:duct/daemon])]
    (log/info "initialized system" ::system-init-success {:system (keys system)})
    (duct/await-daemons system)))

(defn db-config
  [env]
  (:sweet-tooth.endpoint.datomic/connection (init-system env [:duct/database])))

(defn -main
  [cmd & args]
  (let [env (keyword (env/env :app-env :dev))]
    (log/info "-main" ::-main {:cmd cmd})
    (duct/load-hierarchy)
    (case cmd
      "server"
      (start-server env)

      "db/recreate"
      (final env (dbt/recreate-db (db-config env)))

      "db/install-schemas"
      (final env (dbt/install-schemas (db-config env)))

      "db/delete-db"
      (final env (d/delete-database (:uri (db-config env))))

      "deploy/check"
      (final env
             (es/config (keyword env))
             (println "a-ok!")))))
