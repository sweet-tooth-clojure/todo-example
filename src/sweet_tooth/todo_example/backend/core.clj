(ns sweet-tooth.todo-example.backend.core
  (:gen-class)
  (:require [duct.core :as duct]
            [integrant.core :as ig]
            [sweet-tooth.todo-example.backend.duct]
            [taoensso.timbre :as log]
            [sweet-tooth.endpoint.system :as es]
            [sweet-tooth.endpoint.task :as task]
            [sweet-tooth.endpoint.datomic.tasks :as dt]
            [environ.core :as env]))

(duct/load-hierarchy)

(defn init-system
  [env-str profiles]
  (ig/init (es/config (keyword env-str)) profiles))

(defn start-server
  [config]
  (let [system (ig/init config [:duct/daemon])]
    (log/info "initialized system" ::system-init-success {:system (keys system)})
    (duct/await-daemons system)))

(defn -main
  [cmd]
  (let [env    (keyword (env/env :app-env :dev))
        config (es/config env)]
    (log/info "-main" ::-main {:cmd cmd :env env})
    (case cmd
      "server"
      (start-server config)

      "db/recreate"
      (task/run-task-final config ::dt/recreate)

      "db/install-schemas"
      (task/run-task-final config ::dt/install-schemas)

      "db/delete-db"
      (task/run-task-final config ::dt/delete-db)

      "deploy/check"
      (do (println "a-ok!")
          (System/exit 0)))))
