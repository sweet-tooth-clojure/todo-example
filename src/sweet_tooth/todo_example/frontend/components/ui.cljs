(ns sweet-tooth.todo-example.frontend.components.ui
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [sweet-tooth.todo-example.cross.utils :as u]))

(defn focus-child
  [component & [tag-name timeout]]
  (let [tag-name (or tag-name "input")]
    (with-meta (fn [] component)
      {:component-did-mount
       (fn [el]
         (let [dom-node (r/dom-node el)
               node     (if (= (str/lower-case tag-name) (str/lower-case (u/go-get dom-node ["tagName"])))
                          dom-node
                          (first (.getElementsByTagName dom-node tag-name)))]
           (if timeout
             (js/setTimeout #(.focus node) timeout)
             (.focus node))))})))
