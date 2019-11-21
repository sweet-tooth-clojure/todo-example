(ns sweet-tooth.todo-example.backend.db.tasks
  (:require [datomic.api :as d]
            [com.flyingmachine.datomic-booties.core :as datb]
            [taoensso.timbre :as log]))

(defn recreate-db
  [{:keys [uri schema data] :as config}]
  (d/delete-database uri)
  (d/create-database uri)
  (datb/conform (d/connect uri) schema data identity)
  (log/info "db recreated" ::recreate-db))

(defn install-schemas
  [{:keys [uri schema data] :as config}]
  (d/create-database uri)
  (datb/conform (d/connect uri) schema data identity)
  (log/info "schemas installed" ::install-schemas))
