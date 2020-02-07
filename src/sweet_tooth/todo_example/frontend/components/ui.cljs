(ns sweet-tooth.todo-example.frontend.components.ui
  (:require [reagent.core :as r]
            [reagent.ratom :as ratom]
            [clojure.string :as str]
            ["react-transition-group/TransitionGroup" :as TransitionGroup]
            ["react-transition-group/CSSTransition" :as CSSTransition]
            [sweet-tooth.describe :as d]
            [sweet-tooth.frontend.sync.components :as stsc]
            [sweet-tooth.todo-example.cross.utils :as u])
  (:import [goog.async Debouncer]))

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

;;---
;; activity icon
;;---
(def activity-icon [:i.fas.fa-spinner.fa-pulse.activity-indicator])

(defn local-expiring-sub
  [sub timeout & [expired-val]]
  (let [default     (or expired-val nil)
        sub-tracker (r/atom default)
        state       (r/atom default)
        debouncer   (Debouncer. #(reset! state default)
                                timeout)]
    (ratom/make-reaction #(let [sub-val  @sub
                                subt-val @sub-tracker]
                            (when (not= sub-val subt-val)
                              (reset! sub-tracker sub-val)
                              (reset! state sub-val)
                              (.fire debouncer))
                            @state))))

(defn submitting-indicator
  [sync-active?]
  (when @sync-active? activity-icon))

(defn success-indicator
  [state-success? & [opts]]
  (let [expiring-state-success? (local-expiring-sub state-success? 1000)]
    (fn [_state-success? & [opts]]
      [:> TransitionGroup
       {:component "span"
        :className (or (:class opts) "success")}
       (when @expiring-state-success?
         [:> CSSTransition
          {:classNames "fade"
           :timeout    300}
          [:span [:i.fas.fa-check-circle] [:span.success-message " success!"]]])])))

(defn form-state-feedback
  [{:keys [sync-active? state-success?]}]
  [:span.activity-indicator
   [submitting-indicator sync-active?]
   [success-indicator state-success?]])

;;---
;; loadable component
;;---
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
    (loadable-transition [:div "loading... " activity-icon])
    (loadable-transition [:div empty-msg])
    (loadable-transition component)]])

;;---
;; validation with describe
;;---
(defn validate-with
  [rules]
  (fn [form-data]
    (-> (d/describe form-data rules)
        (d/map-rollup-descriptions))))
