(ns py99.test-indent4
  (:require [clojure.string :as str]))

(def edn (read-string (slurp "db-dumps/problems.edn")))

(comment
  (first edn)

  (for [e edn]
    (identity (:test e)))

  (def hello (nth edn 2))

  (str/replace (:test hello) #"\n  (\S)" "\n    $1")
 :rtf)

(spit "db-dumps/indent4.edn"
  (pr-str
    (for [e edn]
      {:problem (:problem e)
       :test (str/replace (:test e) #"\n  (\S)" "\n    $1")})))



