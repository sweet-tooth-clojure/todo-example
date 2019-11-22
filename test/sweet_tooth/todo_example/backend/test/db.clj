(ns sweet-tooth.todo-example.backend.test.db
  (:require [datomic.api :as d]
            [sweet-tooth.endpoint.test.harness :as eth]))

(defn db-config []
  (:sweet-tooth.endpoint.datomic/connection eth/*system*))

(defn conn []
  (d/connect (:uri (db-config))))

(defn db []
  (d/db (conn)))
