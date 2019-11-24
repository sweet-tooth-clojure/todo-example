(ns sweet-tooth.todo-example.cross.utils
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.walk :as walk]
            #?(:clj [byte-transforms :as bt])
            #?(:clj [byte-streams :as bs])
            #?(:cljs [goog.object :as go]))
  (:import #?(:clj org.mindrot.jbcrypt.BCrypt)))

#?(:cljs (defn prevent-default
           [f]
           (fn [e]
             (.preventDefault e)
             (f e))))

#?(:cljs (defn stop-propagation
           [f]
           (fn [e]
             (.stopPropagation e)
             (f e))))

#?(:cljs (defn el-by-id [id]
           (.getElementById js/document id)))

#?(:cljs (defn scroll-top
           []
           (aset (js/document.querySelector "body") "scrollTop" 0)))

(defn tv
  [e]
  (aget e "target" "value"))

(defn capitalize-words 
  "Capitalize every word in a string"
  [s]
  (->> (str/split (str s) #"\b") 
       (map str/capitalize)
       (str/join)))

(defn hkey-text
  [hkey]
  (-> (name hkey)
      (str/replace #"-" " ")
      capitalize-words))

(defn strk
  [key & args]
  (keyword (apply str (name key) args)))

(defn kabob
  [s]
  (str/replace (str s) #"[^a-zA-Z]" "-"))

(defn toggle [v x y]
  (if (= v x) y x))

(defn flatv
  [& args]
  (into [] (flatten args)))

(defn now
  []
  #?(:cljs (js/Date.)
     :clj  (java.util.Date.)))

(defn slugify
  [txt]
  (if txt
    (->> (-> txt
             str/lower-case
             (str/replace #"-+$" "")
             (str/split #"[^a-zA-Z0-9]+"))
         (take 8)
         (str/join "-"))))

(defn pluralize
  [s n & [plural]]
  (if (= n 1) s (or plural (str s "s"))))

;; routes
(defn id-num
  [id-str]
  (re-find #"^\d+" id-str))

(defn topic-url
  [t p]
  (str "/topic/" (:db/id t) "-" (slugify (:topic/title t))))

;; bs

(defn deep-merge-with
  "Like merge-with, but merges maps recursively, applying the given fn
  only when there's a non-map at a particular level.
  (deepmerge + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
               {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}"
  [f & maps]
  (apply
    (fn m [& maps]
      (if (every? map? maps)
        (apply merge-with m maps)
        (apply f maps)))
    maps))

(defn update-vals
  "Takes a map to be updated, x, and a map of
  {[k1 k2 k3] update-fn-1
   [k4 k5 k6] update-fn-2}
  such that such that k1, k2, k3 are updated using update-fn-1
  and k4, k5, k6 are updated using update-fn-2"
  [x update-map]
  (reduce (fn [x [keys update-fn]]
            (reduce (fn [x k]
                      (update x k update-fn))
                    x
                    keys))
          x
          update-map))

(defn some-update-vals
  "like update vals, but only kalls update-fn on key if key exists"
  [x update-map]
  (reduce (fn [x [keys update-fn]]
            (reduce (fn [x k]
                      (if (contains? x k)
                        (update x k update-fn)
                        x))
                    x
                    keys))
          x
          update-map))

#?(:clj (do (defn hash-bcrypt
              [password]
              (BCrypt/hashpw password (BCrypt/gensalt)))

            (defn encode-64
              [s]
              (bs/to-string (bt/encode s :base64 {:url-safe? true})))

            (defn decode-64
              [s]
              (bs/to-string (bt/decode s :base64)))))

#?(:cljs (do (defn go-get
               "Google Object Get - Navigates into a javascript object and gets a nested value"
               [obj ks]
               (let [ks (if (string? ks) [ks] ks)]
                 (reduce go/get obj ks)))
             (defn go-set
               "Google Object Set - Navigates into a javascript object and sets a nested value"
               [obj ks v]
               (let [ks (if (string? ks) [ks] ks)
                     target (reduce (fn [acc k]
                                      (go/get acc k))
                                    obj
                                    (butlast ks))]
                 (go/set target (last ks) v))
               obj)))

(defn projection?
  "Is every value in x present in y?"
  [x y]
  {:pre [(and (seqable? x) (seqable? y))]}
  (let [diff (second (clojure.data/diff y x))]
    (->> (walk/postwalk (fn [x]
                          (when-not (and (map? x)
                                         (nil? (first (vals x))))
                            x))
                        diff)
         (every? nil?))))
