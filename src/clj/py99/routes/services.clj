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

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/actions/:login/:date" {:get actions?}]
   ["/hello" {:get (fn [_] {:status 200 :body "hello"})}]
   ["/problem/:n" {:get fetch-problem}]])
