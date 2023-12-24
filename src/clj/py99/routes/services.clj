(ns py99.routes.services
  (:require
   [clojure.tools.logging :as log]
   [py99.config :refer [period]]
   [py99.db.core :as db]
   [py99.middleware :as middleware]
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

(defn- until-date
  "return a list of `yyyy-mm-dd ` up to today from the day class started."
  [date]
  (remove #(pos? (compare % date)) period))

(defn- s
  [col]
  (let [zeros (count (filter #(= 0 %) col))]
    (* (apply + col) (- 6 zeros))))

(defn s-point-login-date
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
    (log/info "s-point-login-date" py99 sp)
    (response/ok {:login login
                  :date date
                  :py99 py99
                  :s sp})))

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/actions/:login/:date" {:get actions?}]
   ["/hello" {:get (fn [_] {:status 200 :body "hello"})}]
   ["/problem/:n" {:get fetch-problem}]
   ["/s/:login/:date" {:get s-point-login-date}]])
