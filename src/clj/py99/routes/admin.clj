(ns py99.routes.admin
  (:require
   #_[clojure.edn :as edn]
   [clojure.java.io :as io]
   clojure.pprint
   [clojure.string :as str]
   #_[clojure.string :refer [split-lines starts-with? replace-first]]
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [ring.util.response :refer [redirect]]
   [clojure.string :as s]))

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

(defn- p1
  [s]
  (-> s
      (str/replace #"\r" "")
      (str/replace #"\n\s+" " ")))

(defn- p2
  [s]
  (if s
    (str/replace s #"\r\n" "")
    ""))

(defn- pretty-print
  [col]
  (clojure.pprint/pprint
   (for [{:keys [problem test]} col]
     {:problem (p1 problem)
      :test (p2 test)})))

;; FIXME: lazy seq
(defn dump-problems!
  "output problems to resources/docs/problems-dump.edn"
  [_]
  (let [problems (db/fetch-problems)]
    (def it problems)
    (spit (io/resource "docs/problems-dump.edn")
          (with-out-str (pretty-print problems)))
    {:status 200
     :headers {"content-type" "text/html"}
     :body "dumped to resouces/docs/problems-dump.edn"}))

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
