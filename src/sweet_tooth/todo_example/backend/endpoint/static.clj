(ns sweet-tooth.todo-example.backend.endpoint.static
  (:require [ring.util.response :as resp]
            [sweet-tooth.endpoint.liberator :as el]
            [liberator.representation :as lr]
            [integrant.core :as ig]))

(defmethod ig/init-key ::handler [_ _]
  (fn [req]
    (-> (resp/resource-response "index.html" {:root "public"})
        (resp/content-type "text/html"))))
