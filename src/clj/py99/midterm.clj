(ns py99.midterm
  (:require
   [clojure.tools.logging :as log]
   [py99.db.core :as db]
   [py99.routes.home :refer [pytest-test expand-includes]]))

(defn- yyyy-mm-dd [jt]
  (subs (str jt) 0 10))

(defn make-before? [date]
  (fn [row]
    (neg? (compare (yyyy-mm-dd (:create_at row)) date))))

;; (def before-12-15? (make-before? "2022-12-15"))
;;
;; (defn fetch-answers
;;   "fetch answers to problem number `num`."
;;   [num]
;;   (filter before-12-15? (db/answers-to {:num num})))

(defn fetch-answers
  [date num]
  (let [flt (make-before? date)]
    (filter flt (db/answers-to {:num num}))))

(defn save-as!
  "if already saved as `good`, will not overwrite."
  [grade {:keys [num login id]}]
  (log/debug "save-as!" num grade id login)
  (db/create-midterm-result!
   {:num num :login login :answer_id id :grading grade}))

(defn grading
  "grading midterm `num` answers."
  [date num]
  (doseq [answer (fetch-answers date num)]
    (try
      ;; FIXED: forgot expand-includes, 2022-12-14.
      (pytest-test num (expand-includes (:answer answer) (:login answer)))
      (save-as! "good" answer)
      (catch Exception _
        ;; (println (.getMessage e))
        (save-as! "bad" answer)))))

(defn update-midterm!
  "must `poetry shell` before this.
   after clear midterm table, update it."
  []
  (let [mt-nums [211 212 213 214
                 221 222 223 224
                 231 232 233 234
                 241 242 243 244
                 ;; for absent students
                 251 252 253 254]]
    (log/info "update-midterm!")
    (doall (pmap (partial grading "2022-12-15") mt-nums))))

(defn update-reexam!
  []
  (let [re-nums [311 312 313 314
                 321 322 323 324]]
    (log/info "update-reexam!")
    (doall (pmap (partial grading "2022-12-24") re-nums))))

(comment
  (grading "2022-12-24" 313)
  :rcf)

(defn update!
  []
  (try
    (log/info "db/clear-midterm!")
    (db/clear-midterm!)
    (update-midterm!)
    (update-reexam!)
    (log/info "done")
    (catch Exception e (.getMessage e))))

(comment
  ;; don't forget poetry
  (time (update!))
  :rcf)
