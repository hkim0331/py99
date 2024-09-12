(ns py99.routes.admin
  (:require
   [clojure.java.io :as io]
   clojure.pprint
   [clojure.string :as str]
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [ring.util.response :refer [redirect]]))

(def ^:private dump-file (io/resource "docs/problems.edn"))

(defn seed-problems!
  "rebuild problems table from resources/docs/problems.edn.
   before rebuilding, delete all problems from table."
  [request]
  (let [problems (-> dump-file
                     slurp
                     read-string)]
    (db/delete-problems-all!)
    (doseq [[i m] (map-indexed #(vector (inc %1) %2) problems)]
      (db/create-problem! {:num i :problem (:problem m) :test (:test m)}))
    (-> (redirect "/admin")
        (assoc :flash {:doc "seeded problems."}))
    #_(layout/render request "home.html" {:docs "seeded problems."})))

(defn- p1
  [s]
  (-> s
      (str/replace #"\r" "")
      (str/replace #"\n\s*" " ")))

(defn- p2
  [s]
  (or s ""))

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
    ;; (def it problems)
    (spit dump-file
          (with-out-str (pretty-print problems)))
    (-> (redirect "/admin")
        (assoc :flash {:doc "dumped"}))
    #_{:status 200
     :headers {"content-type" "text/html"}
     :body "<h2>dumped to resouces/docs/problems.edn</h2>"}))

(defn admin-page
  [{:keys [flash] :as request}]
  (prn "flash" (:flash request))
  (layout/render request "admin.html"
                 {:flash flash
                  :today (.format
                          (java.text.SimpleDateFormat. "yyyy-MM-dd")
                          (java.util.Date.))}))

(defn problems-all-page [request]
  (layout/render request "edit-problems.html" {:problems (db/problems-all)}))

(defn update-problem! [{:keys [params]}]
  (let [q (update (update params :id #(Integer/parseInt %))
                  :num
                  #(Integer/parseInt %))
        ret (db/update-problem! q)]
    (if (= 1 ret)
      (redirect "/admin/problems")
      (redirect "/error.html"))))

(defn user-actions-page [{{:keys [login date]} :params :as request}]
  (layout/render request
                 "user-actions-page.html"
                 {:actions (db/actions? {:login login :date date})
                  :login login
                  :date date}))

(defn admin-routes []
  ["/admin"
   {:middleware [middleware/admin
                 middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["" {:get  admin-page}]
   ["/user-actions" {:get user-actions-page}]
   ["/problems" {:get problems-all-page
                 :post update-problem!}]
   ["/seed-problems" {:post seed-problems!}]
   ["/dump-problems" {:post dump-problems!}]])
