(ns py99.config
  (:require
   [cprop.core :refer [load-config]]
   [cprop.source :as source]
   [java-time.api :as jt]
   [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config
   :merge
   [(args)
    (source/from-system-props)
    (source/from-env)]))

;; Replaced 2024-09-07
;; (defn- make-period
;;   "return a list of days from `yyyy-mm-dd` to days after from it."
;;   [yyyy mm dd days]
;;   (let [start-day (l/to-local-date-time (t/date-time yyyy mm dd))]
;;     (->> (take days (p/periodic-seq start-day (t/days 1)))
;;          (map to-date-str))))

(defn- make-period
  "return a list of days from `yyyy-mm-dd` to days after from it."
  [yyyy mm dd n]
  (let [start-day (jt/local-date yyyy mm dd)]
    (->> (iterate #(jt/+ % (jt/days 1)) start-day)
         (take n))))

(def period
  "2024-10-01 から 150 日間。"
  (->> (make-period 2024 10 1 150)
       (map str)))

(def weeks
  "授業期間中の月曜日。"
  (->> (make-period 2024 10 1 150)
       (filter jt/monday?)
       (map str)))
