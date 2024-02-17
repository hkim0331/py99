(ns py99.config
  (:require
   [clj-time.core :as t]
   [clj-time.local :as l]
   [clj-time.periodic :as p]
   ;; [clojure.java.io :as io]
   [cprop.core :refer [load-config]]
   [cprop.source :as source]
   [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config
   :merge
   [(args)
    (source/from-system-props)
    (source/from-env)]))


(def weeks
  "weekly reports の〆切り日．2023年度情報応用では月曜日"
  ["2023-10-02" "2023-10-09" "2023-10-16" "2023-10-23" "2023-10-30"
   "2023-11-06" "2023-11-13" "2023-11-20" "2023-11-27"
   "2023-12-04" "2023-12-11" "2023-12-18" "2023-12-25"
   "2024-01-01" "2024-01-08" "2024-01-15" "2024-01-22" "2024-01-29"
   "2024-02-05" "2024-02-12" "2024-02-19"])

(defn- to-date-str
  "FIXME: strongly depends on format of `s`."
  [s]
  (-> (str s)
      (subs 0 10)))

(defn- make-period
  "return a list of days from `yyyy-mm-dd` to days after from it."
  [yyyy mm dd days]
  (let [start-day (l/to-local-date-time (t/date-time yyyy mm dd))]
    (->> (take days (p/periodic-seq start-day (t/days 1)))
         (map to-date-str))))

(def period
  "2023-10-01 から 150 日間．"
  (make-period 2023 10 1 150))
