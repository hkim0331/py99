(ns py99.routes.services
  (:require
   [clojure.tools.logging :as log]
   [py99.config :refer [period weeks]]
   [py99.db.core :as db]
   [py99.middleware :as middleware]
   [py99.utils :as u]
   [ring.util.http-response :as response]))

;;;;;;;;;;;;;;;;;;;;;;
;; from home.clj
;; (defn- before? [s1 s2]
;;   ;; 2022-10-20 s/</<=/
;;   (<= (compare s1 s2) 0))

;; (defn- count-up [m]
;;   (reduce + (map :count m)))

;; (defn bin-count
;;   [data bin]
;;   (loop [data data bin bin ret []]
;;     (if (empty? bin)
;;       ret
;;       (let [g (group-by #(before? (:create_at %) (first bin)) data)
;;             f (g true)
;;             s (g false)]
;;         (recur s (rest bin) (conj ret (count-up f)))))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fetch-problem
  [{{:keys [n]} :path-params}]
  ;; (prn "fetch-problem" n)
  (response/ok (db/get-problem {:num (Integer/parseInt n)})))

(comment
  (db/get-problem {:num 3})
  (db/actions? {:login "hkimura" :date "2023-10-20"})
  :rcf)

;; need auth?
(defn actions?
  [{{:keys [login date]} :path-params}]
  (let [ret (db/actions? {:login login :date date})]
    (response/ok ret)))

(defn- until-date
  "return a list of `yyyy-mm-dd ` up to today from the day class started."
  [date]
  (remove #(pos? (compare % date)) period))

(defn- s
  [col]
  (let [zeros (count (filter #(= 0 %) col))]
    (* (apply + col) (- 6 zeros))))

(defn s-point-login-date
  "calc `login`s s-point from star to until `date`."
  [{{:keys [login date]} :path-params}]
  (let [date-count (db/answers-by-login-date
                    {:login login :date date})
        dc (apply merge (for [mm date-count]
                          {(:create_at mm) (:count mm)}))
        py99 (->> (map #(get dc % 0) (until-date date))
                  reverse
                  (partition 7)
                  (take 3))
        sp (->> py99
                (map s)
                (apply +))]
    (log/debug "s-point-login-date" py99 sp)
    ;; Content-Type: application/json; charset=utf-8 で返る。
    ;; middleware が賢いのか？
    (response/ok {:login login
                  :date date
                  :py99 py99
                  :s sp})))

(defn points [{{:keys [login]} :path-params}]
  (response/ok
   (-> (db/points? {:login login}))))

;; update は grading の仕事．
(defn py99 [{{:keys [login]} :path-params}]
  (response/ok
   {:login login
    :py99 (u/bin-count (db/answers-by-date-login {:login login}) weeks)}))

(defn comm [{{:keys [login]} :path-params}]
  (response/ok
   {:login login
    :comm (u/bin-count (db/comments-by-date-login {:login login}) weeks)}))

(defn goal-in [{{:keys [login]} :path-params}]
  (response/ok
   (assoc (db/solved-by {:login login}) :login login)))

(defn py99!
  [{{:keys [secret login col pt]} :params}]
  (if (= secret (System/getenv "PY99_PASSWORD"))
    (let [pt (Integer/parseInt pt)]
      (case col
        "e1" (db/update-e1! {:login login :pt pt})
        "e2" (db/update-e2! {:login login :pt pt})
        "e3" (db/update-e3! {:login login :pt pt})
        "e4" (db/update-e4! {:login login :pt pt})
        "e5" (db/update-e5! {:login login :pt pt})
        "wil" (db/update-wil! {:login login :pt pt})
        "py99" (db/update-py99! {:login login :pt pt})
        "comm" (db/update-comm! {:login login :pt pt})
        "goal-in" (db/update-goal! {:login login :pt pt})
        "seven-four" (db/update-seven-four! {:login login :pt pt})
        "default")
      (response/ok {:login login
                    :col col
                    :pt pt
                    :secret secret}))
    (response/bad-request {:login login
                           :col col
                           :pt pt
                           :secret secret})))

(defn service-routes
  []
  ["/api" {:middleware [middleware/wrap-formats]}
   ["/actions/:login/:date" {:get actions?}]
   ["/points/:login" {:get points}]
   ["/problem/:n" {:get fetch-problem}]
   ["/s/:login/:date" {:get s-point-login-date}]
   ["/comm/:login" {:get comm}]
   ["/py99/:login" {:get py99}]
   ["/goal-in/:login" {:get goal-in}]
   ;; update
   ["/py99" {:post py99!}]])
