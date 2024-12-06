(ns py99.routes.services
  (:require
   [clojure.tools.logging :as log]
   [py99.config :refer [period weeks]]
   [py99.db.core :as db]
   [py99.middleware :as middleware]
   [py99.utils :as u]
   [ring.util.http-response :as response]))

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

;---------------------------------------------------

(defn- until-date
  "return a list of `yyyy-mm-dd ` up to today from the day class started."
  [date]
  (remove #(pos? (compare % date)) period))

(defn point-f
  [login date f display]
  (let [date-count (db/answers-by-login-date
                    {:login login :date date})
        dc (apply merge (for [mm date-count]
                          {(:create_at mm) (:count mm)}))
        py99 (->> (map #(get dc % 0) (until-date date))
                  reverse
                  (partition 7)
                  (take 3))
        pt (->> py99
                (map f)
                (apply +))]
    (log/debug "point-f" py99 pt)
    (response/ok {:login login
                  :date date
                  :py99 py99
                  display pt})))

(defn- s [col]
  (let [zeros (count (filter #(= 0 %) col))]
    (* (apply + col) (- 6 zeros))))

(defn- sq [x] (* x x))

(defn- p
  "pantsman point 2024-12-05."
  [col]
  (let [avg (/ (reduce + col) (count col))
        sd  (reduce + (map #(sq (- % avg)) col))]
    (/ 7 (+ 1.5 sd))))

(comment
  (p [1, 1, 1, 1, 1, 1, 0])
  (p [3, 3, 3, 3, 3, 3, 3])
  (p [0, 0, 0, 0, 0, 0, 6])
  (p [0,0,4,8,2,0,1])
  (p [0,2,1,1,1,0,3])
  (p [1,0,1,7,0,2,0])
  :rcf)

(defn- o
  "oki-2004 point 2024-12-05"
  [coll]
  (let [count1 (atom 0)
        count0 (atom 0)
        renzoku1 (atom 0)
        renzoku0 (atom 0)
        score (atom 0)]
    (doseq [p coll]
      (when (not (zero? p))
        (swap! count1 inc)
        (reset! renzoku0 (max @renzoku0 @count0))
        (reset! count0 0))
      (when (zero? p)
        (swap! count0 inc)
        (reset! renzoku1 (max @renzoku1 @count1))
        (reset! count1 0)))
    (reset! renzoku1 (max @renzoku1 @count1))
    (reset! renzoku0 (max @renzoku0 @count0))
    (reset! score (* (reduce + coll) (+ 1 (/ (- @renzoku1 @renzoku0) 10))))
    (int (* 2.14 @score))))

(comment
  (o [0 3 0 0 2 0 3])
  (o [2 3 3 0 0 0 0])
  (o [1 1 1 1 1 1 1])
  (o [2 2 2 2 2 2 2])
  (o [0 0 6 0 0 0 0])
  :rcf)

(defn s-point
  "a_syouko09's answer 2023-12-05 13:51:46,
   calc `login`s s-point from start to `date`."
  [login date]
  (-> (point-f login date s :s-point)))

(defn p-point
  [login date]
  (-> (point-f login date p :p-point)))

(defn o-point
  [login date]
  (-> (point-f login date o :o-point)))

(defn s-point-login-date
  [{{:keys [login date]} :path-params}]
  (s-point login date))

;--------------------------------------------

(defn points [{{:keys [login]} :path-params}]
  (response/ok
   (-> (db/points? {:login login}))))

(defn pt
  "returns `login`s sum of points."
  [{{:keys [login]} :path-params}]
  (let [{:keys [py99 comm goal seven_four m1 m2 m3 e1 e2 e3 e4 e5]}
        (db/points? {:login login})
        pt (+ py99 comm goal seven_four e1 e2 e3 e4 e5 (max m1 m2 m3))]
    (response/ok {:login login :pt pt})))

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

; (defn py99!
;   [{{:keys [secret login col pt]} :params}]
;   (if (= secret (System/getenv "PY99_PASSWORD"))
;     (let [pt (Integer/parseInt pt)]
;       (case col
;         "e1" (db/update-e1! {:login login :pt pt})
;         "e2" (db/update-e2! {:login login :pt pt})
;         "e3" (db/update-e3! {:login login :pt pt})
;         "e4" (db/update-e4! {:login login :pt pt})
;         "e5" (db/update-e5! {:login login :pt pt})
;         "wil" (db/update-wil! {:login login :pt pt})
;         "py99" (db/update-py99! {:login login :pt pt})
;         "comm" (db/update-comm! {:login login :pt pt})
;         "goal-in" (db/update-goal! {:login login :pt pt})
;         "seven-four" (db/update-seven-four! {:login login :pt pt})
;         "default")
;       (response/ok {:login login
;                     :col col
;                     :pt pt
;                     :secret secret}))
;     (response/bad-request {:login login
;                            :col col
;                            :pt pt
;                            :secret secret})))

(defn recents [{{:keys [n]} :path-params}]
  (println "recents" n)
  (response/ok (->> (db/recent-answers {:n (parse-long n)})
                    (map #(select-keys % [:num :login :create_at])))))

(defn service-routes
  []
  ["/api" {:middleware [middleware/wrap-formats]}
   ["/actions/:login/:date" {:get actions?}]
   ; ["/comm/:login" {:get comm}]
   ; ["/goal-in/:login" {:get goal-in}]
   ; ["/points/:login" {:get points}]
   ; ["/problem/:n" {:get fetch-problem}]
   ; ["/pt/:login" {:get pt}]
   ; ["/py99/:login" {:get py99}]
   ["/s/:login/:date" {:get s-point-login-date}]
   ; ["/recents/:n" {:get recents}]
   ; ["/py99" {:post py99!}]
   ])
