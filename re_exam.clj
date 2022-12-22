#!/usr/bin/env bb
(ns re-exam
  (:require
    [babashka.pods :as pods]))

(pods/load-pod 'org.babashka/postgresql "0.1.1")
(require '[pod.babashka.postgresql :as pg])

(def db {:dbtype "postgresql"
         :host   "localhost"
         :port   5432
         :dbname (System/getenv "PY99_DB")
         :user   (System/getenv "PY99_USER")
         :password (System/getenv "PY99_PASSWORD")})

(def nums [311 312 313 314 321 322 323 324])

(defn up [nums]
  (doseq [num nums]
    (pg/execute! db ["insert into problems (num, problem) values (?,'')" num])))

(defn down []
  (pg/execute! db ["delete from problems where num > 300"]))

(defn select>300 []
    (pg/execute! db ["select * from problems where num > 300"]))

(defn -main []
  (let [cmd (first *command-line-args*)]
    (case cmd
      "up" (up nums)
      "down" (down)
      "list" (select>300)
      (println "usage:" *file* "up|down"))))

(-main)
