(ns py99.routes.admin
  (:require
   [clojure.java.io :as io]
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [clojure.string :refer [split-lines starts-with? replace-first]]
   [ring.util.response :refer [redirect]]))

(defn- strip-li
  "strip <li> and </li> from s"
  [s]
  (replace-first (replace-first s #"^<li>" "") #"</li>$" ""))

(defn seed-problems!
  "rebuild problems table from docs/seed-problems.html."
  [request]
  (let [num (atom 0)]
    (db/delete-problems-all!)
    (doseq [s (-> "docs/seed-problems.html" io/resource slurp split-lines)]
      (when (starts-with? s "<li>")
        (db/create-problem! {:problem (strip-li s) :num (swap! num inc)}))))
  (layout/render request "home.html" {:docs "seed problems done."}))

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

;;(defn users-page [request])

(defn comments-page [request]
  (let [from (db/comments-from)
        to   (db/comments-to)]
    (layout/render request "comments.html" {:from from
                                            :to to})))

(defn freeze! [request])

(defn frozen? [request])

(defn defrost! [request])

(defn frozens [request])

(defn admin-routes []
  ["/admin"
   {:middleware [middleware/admin
                 middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get  admin-page}]
   ["/problems" {:get problems-page
                 :post update-problem!}]
   ;;["/users"    {:get users-page}]
   ;;["/comments" {:get comments-page}]
   ["/seed-problems" {:post seed-problems!}]
   ["/freeze/:num"  {:post freeze!}]
   ["/froze/:num"   {:get frozen?}]
   ["/defrost/:num" {:post defrost!}]
   ["/frozens"      {:get frozens}]])
