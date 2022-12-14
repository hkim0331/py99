(ns py99.routes.home
  (:require
   [clj-time.core :as t]
   [clj-time.local :as l]
   [clj-time.periodic :as p]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [digest]
   [jx.java.shell :refer [timeout-sh]]
   [py99.charts :refer [class-chart individual-chart comment-chart]]
   [py99.config :refer [env]]
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [py99.routes.login :refer [get-user]] ;; 0.40.0
   [ring.util.response :refer [redirect]]
   [selmer.filters :refer [add-filter!]]))

;; https://stackoverflow.com/questions/16264813/
;;         clojure-idiomatic-way-to-call-contains-on-a-lazy-sequence
(defn- lazy-contains? [col key]
  (some #{key} col))

(defn- to-date-str [s]
  (-> (str s)
      (subs 0 10)))

(defn- make-period
  [yyyy mm dd days]
  (let [start-day (l/to-local-date-time (t/date-time yyyy mm dd))]
    (->> (take days (p/periodic-seq start-day (t/days 1)))
         (map to-date-str))))

(def ^:private period (make-period 2022 10 3 140))
(def ^:private weeks
  ["2022-10-03" "2022-10-10" "2022-10-17" "2022-10-24" "2022-10-31"
   "2022-11-07" "2022-11-14" "2022-11-21" "2022-11-28"
   "2022-12-05" "2022-12-12" "2022-12-19" "2022-12-26"
   "2023-01-02" "2023-01-09" "2023-01-16" "2023-01-23" "2023-01-30"
   "2023-02-06" "2023-02-13"])

;; Selmer private extensions
(defn- wrap-aux
  [n s]
  (if (< (count s) n)
    s
    (str (subs s 0 n) "\n" (wrap-aux n (subs s n)))))

(defn- wrap
  "fold string `s` at column `n`"
  [n s]
  (str/join "\n" (map (partial wrap-aux n) (str/split-lines s))))

(defn- first-line
  [s]
  (-> s
      str/split-lines
      first))

(defn- rest-lines-count
  "number of lines except first line.
  if non-zero, return (+ c),
  if zero, return epmty string."
  [s]
  (let [c (-> s
              str/split-lines
              rest
              count)]
    (if (zero? c)
      (str "")
      (str " (+" c " lines)"))))

(add-filter! :wrap66  (fn [x] (wrap 66 x)))
(add-filter! :first-line (fn [x] (first-line x)))
(add-filter! :rest-lines (fn [x] (rest-lines-count x)))

(defn uptime
  "return uptime string. using required `timeout-sh` utility."
  []
  (let [[_ _ & [one five fifteen]]
        (as-> (timeout-sh 1 "uptime") $
          (:out $)
          (re-find #"load average: .*" $)
          (str/split $ #"\s+"))
        _ (println one five fifteen)
        busy (- (int (first one)) (int \0))
        busy-mark (cond
                    (<= 5 busy) "????"
                    (<= 1 busy) "????"
                    :else "????")]
   (str busy-mark " " (str one five fifteen))))

(comment
  (uptime)
  :rcf)

(defn login
  "return user's login as a string. or nobody."
  [request]
  (name (get-in request [:session :identity] :nobody)))

;; FIXME: symbol? or string?
(defn- admin?
  "return is `user` admin?"
  [user]
  ;;(println "admin? user" user)
  ;; see above. name function.
  (or (= user "hkimura") (= user :hkimua)))

(defn- solved?
  [col n]
  {:n n :stat (if (lazy-contains? col n) "solved" "yet")})

(defn status-page
  "Display user's status. How many problems has he/she solved?"
  [request]
  (let [login (login request)
        solved (map #(:num %) (db/answers-by {:login login}))
        individual (db/answers-by-date-login {:login login})
        all-answers (db/answers-by-date)]
    ;; (log/info "status-page" login)
    (layout/render
     request
     "status.html"
     {:login login
      :status (map #(solved? solved %) (map :num (db/problems)))
      :individual-chart (individual-chart individual period 600 150)
      :class-chart (class-chart all-answers period 600 150)
      :recents (db/recent-answers {:n 20})
      :recent-comments (db/recent-comments {:n 20})})))

(defn problems-page
  "display problems."
  [request]
  ;; (log/info "problem-page" (login request))
  (layout/render request "problems.html" {:problems (db/problems)}))



;; FIXME: destructuring
(defn answer-page
  "Take problem number `num` as path parameter, prep answer to the
   problem."
  [request]
  (let [num (Integer/parseInt (get-in request [:path-params :num]))
        problem (db/get-problem {:num num})
        answers (db/answers-to {:num num})
        frozen?  (db/frozen? {:num num})
        uptime (uptime)]
    ;; (log/info "answer-page" (login request))
    ;; ?????? if ????????????
    (if-let [answer (db/get-answer {:num num :login (login request)})]
      (let [answers (group-by #(= (:md5 answer) (:md5 %)) answers)]
        (layout/render request
                       "answer-form.html"
                       {:problem problem
                        :same (answers true)
                        :differ (answers false)
                        :frozen? frozen?
                        :uptime uptime}))
      (layout/render request
                     "answer-form.html"
                     {:problem problem
                      :same []
                      :differ answers
                      :frozen? frozen?
                      :uptime uptime}))))

;; validations
(defn- remove-comments
  "Remove lines starting from #, they are comments in Python."
  [s]
  (apply
   str
   (interpose "\n" (remove #(str/starts-with? % "#") (str/split-lines s)))))

(defn- strip [s]
  (-> s
      (str/replace #"[ \t]" "")
      remove-comments))

(defn- not-empty-test [answer]
  (when-not (re-find #"\S" (strip answer))
    (throw (Exception. "answer is empty"))))

;; changed 2022-12-25, was 60
(def ^:private timeout 30)

(defn pytest-test
  "Fetch testcode from `num`, test string `answer`.
   Throw exception when test fails."
  [num answer]
  (when-let [test (:test (db/get-problem {:num num}))]
    (when (re-find #"\S" test)
      ;; (log/info "test is not empty" test)
      (let [tempfile (java.io.File/createTempFile "python" ".py")]
        (with-open [file (clojure.java.io/writer tempfile)]
          (binding [*out* file]
            (println "#-*- coding: UTF-8 -*-")
            (println answer)
            (println test)))
        (let [ret (timeout-sh timeout "pytest" (.getAbsolutePath tempfile))]
          (log/info "ret" ret)
          (.delete tempfile)
          (when-not (zero? (:exit ret))
            (throw (Exception. (->> (str/split-lines (:out ret))
                                    (filter #(re-find #"^[>E]" %))
                                    (str/join "\n"))))))))))

(defn- get-answer
  "get user login's answer to `num` from db."
  [num login]
  ;; (log/info "get-answer" num login)
  (if-let [ans (:answer (db/get-answer {:num num :login login}))]
    ans
    (throw (Exception. (str "P-" num " ????????????????????????????????????")))))

(defn expand-includes
  "expand `#include` recursively."
  [s login]
  ;; (log/info "expand-includes:" s)
  (str/join
   "\n"
   (for [line (str/split-lines s)]
     (if (str/starts-with? line "#include ")
       (let [[_ num] (str/split line #"\s+")]
         (when-not (re-matches #"\d+" num)
          (throw (Exception. "#include ??????????????????????????????????????????")))
         (expand-includes (get-answer (Integer/parseInt num) login) login))
       line))))

(defn- validate
  "Return nil if all validations success, or raize exeption."
  [num answer login]
  (try
    (not-empty-test (strip answer))
    (pytest-test num (expand-includes answer login))
    nil
    (catch Exception e (throw (Exception. (.getMessage e))))))

(defn create-answer!
  [{{:keys [num answer]} :params :as request}]
  (log/info "create-answer!" (login request) num)
  (try
    (when-not (env :exam-mode)
      (validate (Integer/parseInt num) answer (login request)))
    (db/create-answer!
     {:login (login request)
      :num (Integer/parseInt num)
      :answer answer
      :md5 (-> answer strip digest/md5)})
    (redirect (str "/answer/" num))
    (catch Exception e
      (layout/render request "error.html"
                     {:status 406
                      :message "?????????????????????????????????????????????????????????????????????????????????"
                      :exception (.getMessage e)}))))

(defn comment-form
  "Taking answer id as path-parameter, show the answer with
   comment form."
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))
        answer (db/get-answer-by-id {:id id})
        num (:num answer)
        my-answer (db/get-answer {:num num :login (login request)})
        exam-mode (env :exam-mode)
        uptime (uptime)]
    (if (and my-answer (or (not exam-mode) (< num 200)))
    ;;(if (and my-answer (< num 300))
      (layout/render request "comment-form.html"
                     {:answer   (if exam-mode my-answer answer)
                      :problem  (db/get-problem {:num num})
                      :same-md5 (db/answers-same-md5 {:md5 (:md5 answer)})
                      :comments (when-not exam-mode
                                  (db/get-comments {:a_id id}))
                      :uptime   uptime})
      (layout/render request "error.html"
                     {:status 403
                      :title "Access Forbidden"
                      :message "?????????????????????????????????"}))))

(defn create-comment! [request]
  (let [params (:params request)
        num (Integer/parseInt (:p_num params))]
    (log/debug "create-comment! num:" num)
    (if (db/frozen? {:num num})
      (layout/render request "error.html"
                     {:status 403
                      :title "Py99 is Frozen"
                      :message "??????????????????????????????????????????"})
      (try
        (db/create-comment! {:from_login (login request)
                             :comment (:comment params)
                             :to_login (:to_login params)
                             :p_num num
                             :a_id (Integer/parseInt (:a_id params))})
        (redirect "/")
        (catch Exception _
          (layout/render request "error.html"
                         {:status 406
                          :title "frozen r99"
                          :message "can not add comments"}))))))

(defn comments-sent [request]
  (let [login (get-in request [:path-params :login])
        sent (db/comments-sent {:login login})]
    (layout/render request "comments-sent.html" {:sent sent})))

(defn comments [request]
  ;;(log/info "comments" (login request))
  (layout/render request "comments.html"
                 {:comments (drop 20 (db/comments))}))

(defn comments-by-num [request]
  (let [num (Integer/parseInt (get-in request [:path-params :num]))]
    ;;(log/info "comments-by-num" (login request))
    (layout/render request "comments.html"
                   {:comments (db/comments-by-num {:num num})})))

;;
;; weekly counts
;;
(defn- before? [s1 s2]
  ;; 2022-10-20 s/</<=/
  (<= (compare s1 s2) 0))

(defn- count-up [m]
  (reduce + (map :count m)))

(defn bin-count
  "data ????????????????????????????????? answers ??? comments ?????????????????????
   (count-up f)????????????"
  [data bin]
  (loop [data data bin bin ret []]
    (if (empty? bin)
      ret
      (let [g (group-by #(before? (:create_at %) (first bin)) data)
            f (g true)
            s (g false)]
        (recur s (rest bin) (conj ret (count-up f)))))))

(defn profile [login]
  (let [solved (db/answers-by {:login login})
        individual (db/answers-by-date-login {:login login})
        comments (db/comments-by-date-login {:login login})]
    ;;(log/info "profile who?" {:login login})
    (layout/render {} "profile.html"
                   {:login login
                    :user (get-user login)
                    :chart (individual-chart individual period 600 150)
                    :comment-chart (comment-chart comments period 600 150)
                    :comments-rcvd (db/comments-rcvd {:login login})
                    :comments (db/sent-comments {:login login})
                    :submissions (-> solved count)
                    :solved (->> solved
                                 (map :num)
                                 (remove #(< 200 %))
                                 distinct
                                 count)
                    :last (if (seq solved)
                            (apply max-key :id solved)
                            [])
                    :weekly (map list
                                 weeks
                                 (bin-count individual weeks)
                                 (bin-count comments weeks))
                    :groups (filter #(< 200 (:num %)) solved)})))

(defn profile-self
  [request]
  (profile (login request)))

(defn profile-login
  [request]
  ;; (log/info "profile-login" (login request))
  (if (admin? (login request))
    (profile (get-in request [:path-params :login]))
    (layout/render request "error.html"
                   {:status 403
                    :title "Access Forbidden"
                    :message "admin only. "})))

(defn ranking [request]
  ;; (log/info "ranking" (login request))
  (layout/render request "ranking.html"
                 {:submissions (take 30 (db/submissions))
                  :solved      (take 30 (db/solved))
                  :comments    (take 30 (db/comments-counts))
                  :login (login request)
                  :n 30}))

(defn rank-submissions [request]
  (let [login (login request)]
    ;; (log/info "rank-submissions" login)
    (layout/render request "ranking-all.html"
                   {:data (db/submissions)
                    :title "Ranking Submissions"
                    :login  login
                    :admin? (admin? login)})))

(defn rank-solved [request]
  (let [login (login request)]
    ;; (log/info "rank-solved" login)
    (layout/render request "ranking-all.html"
                   {:data (db/solved)
                    :title "Ranking Solved"
                    :login  login
                    :admin? (admin? login)})))

(defn rank-comments [request]
  (let [login (login request)
        data (map (fn [x] {:login (:from_login x)
                           :count (:count x)})
                  (db/comments-counts))]
    ;; (log/info "rank-comments" login)
    (layout/render request "ranking-all.html"
                   {:data data
                    :title "Comments Ranking"
                    :login  login
                    :admin? (admin? login)})))

(defn answers-by-problems [request]
  (let [data (db/answers-by-problems)]
    ;; (log/info "answers-by-problems" (login request))
    (layout/render request "answers-by-problems.html"
                   {:data data
                    :title "Answers by Problems"})))

(defn create-stock! [request]
  (let [login (login request)
        a_id (-> (get-in request [:params :a_id])
                 Integer/parseInt)]
    (log/info "create-stock!" login)
    (try
      (db/create-stock! {:login login :a_id a_id})
      (redirect (str "/comment/" a_id))
      (catch Exception e
        (layout/render nil "error.html"
                       {:status 406
                        :message "create stock error"
                        :exception (.getMessage e)})))))

(defn list-stocks [request]
  (let [login (login request)]
    (log/info "list-stocks" login)
    (layout/render request "stocks.html"
                   {:stocks (db/stocks? {:login login})})))

(defn list-todays [{{:keys [date]} :path-params :as request}]
  (log/info "list-todays" date)
  (if (re-matches #"\d\d\d\d-\d\d-\d\d" date)
    (layout/render request "todays.html"
                   {:date date
                    :todays (db/todays? {:date date})})
    (layout/render request "error.html"
                   {:status 403
                    :title "date format error"
                    :message "????????????????????????????????????????????????"})))

(defn list-todays-today [request]
  (let [today (to-date-str (l/local-now))]
    (log/info "list-todays-today")
    (layout/render request "todays.html"
                   {:date today
                    :todays (db/todays? {:date today})})))

(defn midterm [request]
  (layout/render request "midterm.html"))

(defn home-routes []
  ["" {:middleware [middleware/auth
                    middleware/wrap-csrf
                    middleware/wrap-formats]}
   ["/" {:get status-page}]
   ["/answers" {:get answers-by-problems}]
   ["/answer/:num" {:get  answer-page
                    :post create-answer!}]
   ["/comment/:id" {:get  comment-form
                    :post create-comment!}]
   ["/comments" {:get comments}]
   ["/comments-sent/:login" {:get comments-sent}]
   ["/comments/:num" {:get comments-by-num}]
   ["/midterm" {:get midterm}]
   ["/problems" {:get problems-page}]
   ["/profile" {:get profile-self}]
   ["/profile/:login" {:get profile-login}]
   ["/ranking" {:get ranking}]
   ["/rank/submissions" {:get rank-submissions}]
   ["/rank/solved"      {:get rank-solved}]
   ["/rank/comments"    {:get rank-comments}]
   ["/stock" {:post create-stock!
              :get  list-stocks}]
   ["/todays" {:get list-todays-today}]
   ["/todays/:date" {:get list-todays}]
   ["/wp" {:get (fn [_]
                  {:status 200
                   :headers {"Content-Type" "text/html"}
                   :body (slurp (io/resource "docs/weekly-points.html"))})}]])
