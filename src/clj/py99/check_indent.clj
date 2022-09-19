(ns py99.check-indent
  (:require
   [clojure.string :as str]
   #_[taoensso.timbre :as timbre]))

(defn- remove-comments
  [lines]
  (map #(str/replace % #"//.*" "") lines))

(defn- remove-blank-lines
  [lines]
  (filter (partial re-find #"\S") lines))

(defn- remove-open-close
  [lines]
  (remove (partial re-find #"\{.*\}") lines))

(defn- remove-close-open
  [lines]
  (remove (partial re-find #"\}.*\{") lines))

(defn- indent
  [s]
  (count (re-find #"^\s*" s)))

(defn- indents
  [lines]
  (map indent lines))

(defn- diff
  [s]
  (map (fn [[a b]] (- b a)) (partition 2 1 s)))

(defn- curly
  [line]
  (cond
    (re-find #"\{" line) 1
    (re-find #"\}" line) -1
    :else 0))

(defn- curlys
  [lines]
  (map curly lines))

(defn- check-aux
  [diff curls only]
  (let [d (concat (map only diff) [0])
        c (map only curls)]
    (= d c)))

(defn- expand-tabs
  [s]
  (str/replace s #"\t" "  "))

(defn- skel [s]
  (-> s
      expand-tabs
      str/split-lines
      remove-comments
      remove-blank-lines
      remove-close-open
      remove-open-close))

;; indent any
(defn- normalize [v]
  (let [c (first (remove zero? v))]
    (map #(/ % c) v)))

(defn check-indent [s]
  (let [lines (skel s)
        indents (indents lines)
        diffs (normalize (diff indents))
        curls (curlys lines)]
    (when-not (and
               (< 0 (reduce + indents))
               (every? even? indents)
               (check-aux diffs curls #(if (= 1 %) 1 0))
               (check-aux (reverse diffs) (reverse curls) #(if (= -1 %) -1 0)))
      (throw (Exception. "R99 のインデントルールに抵触してます。")))))

(comment
  (try
    (check-indent (slurp "indent-check.c"))
    (catch Exception e (.getMessage e))))
