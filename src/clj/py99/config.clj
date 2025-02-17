(ns py99.config
  (:require
   [clojure.string :as str]
   [cprop.core :refer [load-config]]
   [cprop.source :as source]
   [environ.core :as environ]
   [java-time.api :as jt]
   [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config
   :merge
   [(args)
    (source/from-system-props)
    (source/from-env)]))

(defn- make-period
  "return a list of days from `yyyy-mm-dd` to days after from it."
  [yyyy mm dd n]
  (let [start-day (jt/local-date yyyy mm dd)]
    (->> (iterate #(jt/+ % (jt/days 1)) start-day)
         (take n))))

(comment
  (environ/env :py99-start)
  ; => "2024-10-01"
  (environ/env :py99-days)
  ; => "150"
  )

;; FIXME: mandatory env
;; env がなくてもエラーにしない。
(let [start (or (environ/env :py99-start) "2024-10-01")
      days (or (environ/env :py99-days) "150")
      [year month day] (map parse-long
                            (-> start
                                (str/split #"-")))
      days (parse-long days)]
  (println "start" year month day "," days)
  (def py99-period
    "2024-10-01 から150日間。"
    (make-period (or year 2024)
                 (or month 10)
                 (or day 1)
                 (or days 150))))
(def period
  (->> py99-period
       (map str)))

(def weeks
  "sunday 23:59:00 is the weekly deadline."
  (->> py99-period
       (filter jt/sunday?)
       (map str)))
