(ns sweet-tooth.todo-example.backend.test.data
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [com.flyingmachine.datomic-junk :as dj]
            [datomic.api :as d]
            [reifyhealth.specmonstah.core :as rs]
            [reifyhealth.specmonstah.spec-gen :as rsg]
            [sweet-tooth.todo-example.backend.test.db :as tdb]))

(s/def :db/id (s/with-gen
                (s/or :integer pos-int?
                      :datomic-id #(instance? datomic.db.DbId %))
                #(gen/return (d/tempid :db.part/user))))
(s/def :db/ref :db/id)
(s/def :common/not-empty-string (s/and string? not-empty))

(s/def :todo-list/title :common/not-empty-string)
(s/def :entity/todo-list (s/keys :req [:db/id :todo-list/title]))

(def schema
  {:todo-list {:prefix :tl
               :spec   :entity/todo-list}
   :todo      {:prefix    :t
               :relations {:todo/todo-list [:todo-list :db/id]}}})

(defn gen1
  [spec]
  (gen/generate (s/gen spec)))

(defn spec-gen-map
  [db]
  (rs/attr-map db rsg/spec-gen-visit-key))

(defn transact!
  [query]
  (let [sm-db  (rsg/ent-db-spec-gen {:schema schema} query)
        result @(d/transact (tdb/conn) (vals (spec-gen-map sm-db)))]
    (-> (rs/visit-ents-once sm-db :transact! (fn [db {:keys [spec-gen]}]
                                               (d/resolve-tempid (:db-after result)
                                                                 (:tempids result)
                                                                 (:db/id spec-gen))))
        (rs/attr-map :transact!))))
