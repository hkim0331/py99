(ns py99.routes.services
  (:require
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

;; (defn s-point-login
;;   [{{:keys [login]} :path-params}]
;;   (log/info "s-point-days" login)
;;   (let [date-count (db/answers-by-date-login {:login login})
;;         dc (apply merge (for [mm date-count]
;;                           {(:create_at mm) (:count mm)}))]
;;     (log/info "dc" dc)
;;     (response/ok (map #(get dc % 0) (up-to-today)))))

;; (defn s-point
;;   [request]
;;   (log/info "s-point" (login request))
;;   (s-point-login {:path-params {:login (login request)}}))

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/actions/:login/:date" {:get actions?}]
   ["/hello" {:get (fn [_] {:status 200 :body "hello"})}]
   ["/problem/:n" {:get fetch-problem}]
  ;;  ["/s" {:get s-point}]
  ;;  ["/s/:login" {:get s-point-login}]
  ;;  ["/s/:login/:date" {:get s-point-login-date}]
  ])
