(ns py99.routes.services
  (:require
   [py99.db.core :as db]
   [py99.middleware :as middleware]
   [ring.util.http-response :as response]))

(defn midterm [{{:keys [n]} :path-params}]
  (response/ok {:n n}))

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ["/midterm/:n" {:get midterm}]])
