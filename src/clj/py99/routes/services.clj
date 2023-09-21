(ns py99.routes.services
  (:require
   #_[py99.db.core :as db]
   [py99.middleware :as middleware]
   [ring.util.http-response :as response]))

;; (defn midterm [{{:keys [n]} :path-params}]
;;   (response/ok {:n n}))

;; FIXME: under construction
(defn fetch-problem
  [{{:keys [n]} :path-params}]
  ;; (prn "fetch-problem" n)
  (response/ok {:fetch (Integer/parseInt n)}))

(comment
  (response/ok "n")
  :rcf)

(defn service-routes []
  ["/api"
   {:middleware [middleware/wrap-formats]}
   ;;["/midterm/:n" {:get midterm}]
   ["/hello" {:get (fn [_] {:status 200 :body "hello"})}]
   ["/problem/:n" {:get fetch-problem}]
   ])
