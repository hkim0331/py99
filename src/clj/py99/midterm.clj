(ns py99.midterm
  (:require
   [clojure.tools.logging :as log]
   [py99.db.core :as db]
   [py99.routes.home :refer [pytest-test]]))

;; (defn- short [s]
;;   (if (< (count s) 20)
;;     s
;;     (subs s 0 20)))

(defn fetch-answers
  "fetch answers to problem number `num`."
  [num]
  (db/answers-to {:num num}))

;; (defn is-good?
;;   [num login answer_id]
;;   (db/midterm-))

(defn save-as!
  "if already saved as `good`, will not overwrite."
  [grade {:keys [num login id]}]
  (log/debug "save-as!" num grade id login)
  (db/create-midterm-result!
   {:num num :login login :answer_id id :grading grade}))

(defn grading
  "grading midterm `num` answers."
  [num]
  (doseq [answer (fetch-answers num)]
    (try
      ;; (log/debug "answer" (short answer))
      (pytest-test num (:answer answer))
      (save-as! "good" answer)
      (catch Exception _
        ;; (println (.getMessage e))
        (save-as! "bad" answer)))))

(defn update-midterm
  "must `poetry shell` before this."
  []
  (let [mt-nums [211 212 213 214
                 221 222 223 224
                 231 232 233 234
                 241 242 243 244
                 251 252 253 254]]
    (db/clear-midterm!)
    (map grading mt-nums)))