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

(def period-2023
  "2023-10-01 から150日間。"
  (make-period 2023 10 1 150))

(def period-2024
  "2024-10-01 から150日間。"
  (make-period 2024 10 1 150))

(def period
  (->> period-2023
       (map str)))

(def weeks
  "monday 23:59:00 is the weekly deadline."
  (->> period-2023
       (filter jt/monday?)
       (map str)))
