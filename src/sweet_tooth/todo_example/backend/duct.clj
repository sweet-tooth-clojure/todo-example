(ns sweet-tooth.todo-example.backend.duct
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as buddy]
            [clojure.java.io :as io]
            [duct.core :as duct]
            [integrant.core :as ig]
            [ring.middleware.gzip :as ring-gzip]
            [sweet-tooth.endpoint.system :as es]))

(duct/load-hierarchy)

(defmethod ig/init-key ::buddy [_ config]
  #(buddy/wrap-authentication % (backends/session)))

;;--------------------
;; middleware integrant
;;--------------------

(defmethod ig/init-key ::wrap-cors [_ config]
  (fn [handler]
    (fn [req]
      (let [headers {"Access-Control-Allow-Origin" "http://localhost:3000"
                     "Access-Control-Allow-Methods" "GET, PUT, POST, DELETE, OPTIONS"
                     "Access-Control-Allow-Headers" "Content-Type, *"
                     "Access-Control-Allow-Credentials" "true"}]
        (if (= (:request-method req) :options)
          {:status 200 :headers headers :body "preflight complete"}
          (-> (handler req)
              (update :headers merge headers)))))))

(defmethod ig/init-key ::wrap-latency [_ {:keys [sleep]}]
  (fn [handler]
    (fn [req]
      (Thread/sleep sleep)
      (handler req))))

(defmethod ig/init-key ::wrap-gzip [_ config]
  ring-gzip/wrap-gzip)

;; module for more targeted duct config merging
(defmethod ig/init-key ::merge-many [_ {:keys [configs]}]
  (fn [config]
    (reduce (fn [config c]
              (duct/merge-configs config (update-in c [:duct.handler/root :middleware] #(mapv ig/ref %))))
            config
            configs)))

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

(defmethod es/config :integration
  [_]
  (prep [:duct.profile/test]))

(defmethod es/config :dev
  [_]
  (prep [:duct.profile/dev :duct.profile/local]))

(defmethod es/config :local-staging
  [_]
  (prep [:duct.profile/local-staging]))

(defmethod es/config :staging
  [_]
  (prep [:duct.profile/staging]))

(defmethod es/config :dev-email-test
  [_]
  (dissoc (prep [:duct.profile/dev]) :duct.server.http/jetty))

(defmethod es/config :prod
  [_]
  (prep [:duct.profile/prod]))
