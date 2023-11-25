;; https://github.com/babashka/pod-registry/blob/master/examples/postgresql.clj
(ns midterm)
(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/postgresql "0.1.2")
(require '[pod.babashka.postgresql :as pg])

(def db {:dbtype "postgresql"
         :user (System/getenv "PY99_USER")
         :dbname (System/getenv "PY99_DB")
         :password (System/getenv "PY99_PASSWORD")
         :host "localhost"
         :port "5432"})

(def midterm [211 212 213 214
              221 222 223 224
              231 232 233 234
              241 242 243 244])

(defn midterm-down
  []
  (for [num midterm]
    (pg/execute! db
                 [(str "delete from problems where num = " num ";")])))

(defn midterm-up
  []
  (for [num midterm]
    (pg/execute! db
                 [(str "insert into problems (num, problem) values (" num ",'');")])))


;; (pg/execute! db ["drop table if exists mytable;"])
;; (pg/execute! db ["create table mytable ( foobar int );"])
;; (pg/execute! db ["insert into mytable (foobar) values (3);"])
;; (pg/execute! db ["select * from mytable"])


(comment
  (pg/execute! db ["select now();"])
  (midterm-down)
  (midterm-up)
  :rcf)

