(ns sweet-tooth.todo-example.frontend.components.ui
  (:require [reagent.core :as r]
            [clojure.string :as str]
            ["react-transition-group/TransitionGroup" :as TransitionGroup]
            ["react-transition-group/CSSTransition" :as CSSTransition]
            [sweet-tooth.frontend.sync.components :as stsc]
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

(defn loadable-transition
  [component]
  (when component
    [:> CSSTransition {:classNames "fade" :timeout 300}
     component]))

(defn loadable-component
  [sync-state-sub empty-msg component]
  [:> TransitionGroup {:component "div"}
   [stsc/loadable-component
    sync-state-sub
    (loadable-transition [:div "loading... " [:i.fas.fa-spinner.fa-pulse]])
    (loadable-transition [:div empty-msg])
    (loadable-transition component)]])
