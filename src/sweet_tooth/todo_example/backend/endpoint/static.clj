(ns sweet-tooth.todo-example.backend.endpoint.static
  (:require [ring.util.response :as resp]
            [integrant.core :as ig]))

(defmethod ig/init-key ::handler [_ _]
  (fn [_req]
    (-> (resp/resource-response "index.html" {:root "public"})
        (resp/content-type "text/html"))))
