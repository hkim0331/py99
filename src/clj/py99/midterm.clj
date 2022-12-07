(ns py99.midterm
  (:require
   [py99.db.core :as db]
   [py99.routes.home :refer [pytest-test]]))

(defn fetch-answers
  "fetch answers to problem number `num`."
  [num]
  (db/answers-to {:num num}))

(defn save-good! [answer])

(defn save-bad! [answer])

(defn grading
  [num]
  (doseq [answer (fetch-answers num)]
    (try
      (pytest-test num answer)
      (save-good! answer)
      (catch Exception e
        (save-bad! answer)))))
