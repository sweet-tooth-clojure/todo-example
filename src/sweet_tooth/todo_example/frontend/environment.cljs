(ns sweet-tooth.todo-example.frontend.environment
  (:require [sweet-tooth.todo-example.cross.utils :as u]))

(def environment
  (let [host (u/go-get js/location ["hostname"])
        port (u/go-get js/location ["port"])]
    (cond
      (= host "localhost")                 (case port
                                             "4010" :integration
                                             "3000" :dev
                                             "3010" :local-staging
                                             "3100" :local-staging))))
