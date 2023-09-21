(ns py99.routes.services
  (:require
   [py99.db.core :as db]
   [py99.middleware :as middleware]
   [ring.util.http-response :as response]))

(defn fetch-problem
  [{{:keys [n]} :path-params}]
  (prn "fetch-problem" n)
  (response/ok (db/get-problem {:num (Integer/parseInt n)})))

(comment
  (db/get-problem {:num 3})
:rcf)

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/hello" {:get (fn [_] {:status 200 :body "hello"})}]
   ["/problem/:n" {:get fetch-problem}]])
