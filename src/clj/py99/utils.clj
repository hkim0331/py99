(ns py99.utils
  (:require
   [java-time.api :as jt]
   [py99.config :as conf]))

(defn dev? []
  (= (System/getenv "PY99_DEV") "true"))

;; (dev?)

(defn- before? [s1 s2]
  (<= (compare s1 s2) 0))

(defn- count-up [m]
  (reduce + (map :count m)))

(defn bin-count
  [data bin]
  (loop [data data bin bin ret []]
    (if (empty? bin)
      ret
      (let [g (group-by #(before? (:create_at %) (first bin)) data)
            f (g true)
            s (g false)]
        (recur s (rest bin) (conj ret (count-up f)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn days-from-to
  "days `from` inclusive to `to` exclusive."
  [from to]
  (->> conf/period
       (drop-while #(not= from %))
       (take-while #(not= to %))))

(defn today []
  (str (jt/local-date)))

;; https://stackoverflow.com/questions/16264813/
;; clojure-idiomatic-way-to-call-contains-on-a-lazy-sequence
(defn lazy-contains? [col key]
  (some #{key} col))
