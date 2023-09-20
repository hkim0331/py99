(ns py99.routes.admin
  (:require
   #_[clojure.edn :as edn]
   [clojure.java.io :as io]
   clojure.pprint
   #_[clojure.string :refer [split-lines starts-with? replace-first]]
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [ring.util.response :refer [redirect]]))

;; (defn seed-problems-from-markdown!
;;   "rebuild problems table from docs/problems.md"
;;   [request]
;;   (let [problems (->> (io/resource "docs/problems.md")
;;                       slurp
;;                       split-lines
;;                       (filter #(starts-with? % "1. "))
;;                       (map #(replace-first % #"1. " "")))]
;;     (db/delete-problems-all!)
;;     (doseq [[i line] (map-indexed #(vector (inc %1) %2) problems)]
;;       (db/create-problem! {:num i :problem line}))
;;     (layout/render request "home.html" {:docs "done seed problems."})))

(defn seed-problems!
  "rebuild problems table from resources/docs/problems.edn.
   before rebuilding, delete the problems table."
  [request]
  (let [problems (-> (io/resource "docs/problems.edn")
                     slurp
                     read-string)]
    (db/delete-problems-all!)
    (doseq [[i m] (map-indexed #(vector (inc %1) %2) problems)]
      (db/create-problem! {:num i :problem (:problem m) :test (:test m)}))
    (layout/render request "home.html" {:docs "seeded problems."})))

;; FIXME: pretty print
(defn dump-problems!
  "output problems to resources/docs/problems-dump.edn"
  [_]
  (spit (io/resource "docs/problems-dump.edn")
        (with-out-str (clojure.pprint/pprint (db/fetch-problems))))
  {:status 200
   :headers {"content-type" "text/html"}
   :body "dumped to resouces/docs/problems-dump.edn"})

(defn admin-page [request]
  (layout/render request "admin.html"))

(defn problems-page [request]
  (layout/render request "edit-problems.html" {:problems (db/problems)}))

(defn update-problem! [{:keys [params]}]
  (let [q (update (update params :id #(Integer/parseInt %))
                  :num
                  #(Integer/parseInt %))
        ret (db/update-problem! q)]
    (if (= 1 ret)
      (redirect "/admin/problems")
      (redirect "/error.html"))))

(defn admin-routes []
  ["/admin"
   {:middleware [middleware/admin
                 middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["" {:get  admin-page}]
   ["/problems" {:get problems-page
                 :post update-problem!}]
   ["/seed-problems" {:post seed-problems!}]
   ["/dump-problems" {:post dump-problems!}]])
