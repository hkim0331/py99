(ns py99.midterm
  (:require
   [clojure.tools.logging :as log]
   [py99.db.core :as db]
   [py99.routes.home :refer [pytest-test]]))

(defn- short [s]
  (if (< (count s) 20)
    s
    (subs s 0 20)))

(defn fetch-answers
  "fetch answers to problem number `num`."
  [num]
  (db/answers-to {:num num}))

(defn- save-as! [grade {:keys [num login id]}]
  (log/debug "save-as!" num grade id login))

;; (defn save-good! [answer]
;;   ;; (log/debug "good" (short answer))
;;   (save-as! answer "good"))

;; (defn save-bad! [answer]
;;   ;; (log/info "bad" (short answer))
;;   (save-as! answer "ng"))

(defn grading
  [num]
  (doseq [answer (fetch-answers num)]
    (try
      ;; (log/debug "answer" (short answer))
      (pytest-test num (:answer answer))
      (save-as! "good" answer)
      (catch Exception e
        (println (.getMessage e))
        (save-as! "bad" answer)))))
