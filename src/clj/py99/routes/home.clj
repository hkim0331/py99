(ns py99.routes.home
  (:require
   #_[buddy.hashers :as hashers]
   #_[clj-commons-exec :as exec]
   [clj-time.core :as t]
   [clj-time.local :as l]
   [clj-time.periodic :as p]
   [clojure.java.io :as io]
   [clojure.java.shell :refer [sh]]
   [clojure.string :as str]
   [digest]
   #_[environ.core :refer [env]]
   [py99.charts :refer [class-chart individual-chart comment-chart]]
   #_[py99.check-indent :refer [check-indent]]
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [ring.util.response :refer [redirect]]
   [selmer.filters :refer [add-filter!]]
   [taoensso.timbre :as timbre]))

;; (when-let [level (env :py99-log-level)]
;;  (timbre/set-level! (keyword level)))

;; FIXME: オフにするのはデバッグ時のみか。
(def ^:private validate? true)

;; py99 は2022-10-10 から 130 日間営業
(defn- to-date-str [s]
  (-> (str s)
      (subs 0 10)))

(defn- make-period
  [yyyy mm dd days]
  (let [start-day (l/to-local-date-time (t/date-time yyyy mm dd))]
    (->> (take days (p/periodic-seq start-day (t/days 1)))
         (map to-date-str))))

;;(def ^:private period (make-period 2022 10 10 130))
(def ^:private period (make-period 2022 9 10 160))
(def ^:private weeks
  ["2022-09-10" "2022-09-17" "2022-09-21"
   "2022-10-10" "2022-10-17" "2022-10-24" "2022-10-31"
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

(defn login
  "return user's login as a string. or nobody."
  [request]
  (name (get-in request [:session :identity] :nobody)))

;; FIXME
(defn- admin?
  "return `user` is admin?"
  [user]
  (= user :hkimua))

;; https://stackoverflow.com/questions/16264813/
;;         clojure-idiomatic-way-to-call-contains-on-a-lazy-sequence
(defn- lazy-contains? [col key]
  (some #{key} col))

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
    ;; (timbre/info "status-page" login)
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
  ;; (timbre/info "problem-page" (login request))
  (layout/render request "problems.html" {:problems (db/problems)}))

;; FIXME: destructuring
(defn answer-page
  "Take problem number `num` as path parameter, prep answer to the
   problem."
  [request]
  (let [num (Integer/parseInt (get-in request [:path-params :num]))
        problem (db/get-problem {:num num})
        answers (db/answers-to {:num num})
        frozen?  (db/frozen? {:num num})]
    ;; (timbre/info "answer-page" (login request))
    ;; この if の理由？
    (if-let [answer (db/get-answer {:num num :login (login request)})]
      (let [answers (group-by #(= (:md5 answer) (:md5 %)) answers)]
        (layout/render request
                       "answer-form.html"
                       {:problem problem
                        :same (answers true)
                        :differ (answers false)
                        :frozen? frozen?}))
      (layout/render request
                     "answer-form.html"
                     {:problem problem
                      :same []
                      :differ answers
                      :frozen? frozen?}))))

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

;; (defn- space-rule?
;;   "R99 space-char rules"
;;   [s]
;;   (when-not (every? nil?
;;                     [(re-find #"include<" s)
;;                      (re-find #"\)\{" s)
;;                      (re-find #"if\(" s)
;;                      (re-find #"for\(" s)
;;                      (re-find #"while\(" s)
;;                      (re-find #"}else" s)
;;                      (re-find #"else\{" s)
;;                      (re-find #"\n\s*else" s)
;;                      (re-find #" \+\+" s)
;;                      (re-find #"\+\+\s+[a-zA-Z]" s)])
;;     (throw (Exception. "against R99 space rules"))))

;; FIXME: python
;; https://github.com/hozumi/clj-commons-exec
;; (defn- can-compile? [answer]
;;   (let [r (exec/sh ["gcc" "-xc" "-fsyntax-only" "-"] {:in answer})]
;;     (timbre/debug "gcc" @r)
;;     (when-let [err (:err @r)]
;;       (throw (Exception. err)))))

(defn- pytest-test
  [num answer]
  (let [test (:test (db/get-problem {:num num}))]
    (timbre/info "test" test)
    (when (re-matches #"\S" test)
      (let [tempfile (java.io.File/createTempFile "python" ".py")]
        (with-open [file (clojure.java.io/writer tempfile)]
          (binding [*out* file]
            (println answer)
            (println test)))
        (let [ret (sh "pytest" (.getAbsolutePath tempfile))]
          (timbre/info "ret" ret)
          (.delete tempfile)
          (when-not (zero? (:exit ret))
            (throw (Exception. "test failed."))))))))

(defn- validate
  "Return nil if all validations success, or raize exeption."
  [num answer]
  (try
    (not-empty-test (strip answer))
    (pytest-test num answer)
    nil
    (catch Exception e (throw (Exception. (.getMessage e))))))

(defn create-answer!
  [{{:keys [num answer]} :params :as request}]
  (try
    (validate (Integer/parseInt num) answer)
    (db/create-answer!
     {:login (login request)
      :num (Integer/parseInt num)
      :answer answer
      :md5 (-> answer strip digest/md5)})
    (redirect (str "/answer/" num))
    (catch Exception e
      (layout/render request "error.html"
                     {:status 406
                      :title (.getMessage e)
                      :message "ブラウザのバックで戻って、修正後、再提出してください。"}))))

;; (defn- require-my-answer?
;;   []
;;   (= (env :py99-require-my-answer) "TRUE"))

(defn comment-form
  "Taking answer id as path-parameter, show the answer with
   comment form."
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))
        answer (db/get-answer-by-id {:id id})
        num (:num answer)
        my-answer (db/get-answer {:num num :login (login request)})]
    ;; (timbre/info "comment-form" (login request))
    ;; self-only? を使って書いてた。それは何？
    (if my-answer
      (layout/render request "comment-form.html"
                     {:answer   answer
                      :problem  (db/get-problem {:num num})
                      :same-md5 (db/answers-same-md5 {:md5 (:md5 answer)})
                      :comments (db/get-comments {:a_id id})})
      (layout/render request "error.html"
                     {:status 403
                      :title "Access Forbidden"
                      :message "まず自分で解いてから。"}))))

(defn create-comment! [request]
  (let [params (:params request)
        num (Integer/parseInt (:p_num params))]
    (timbre/debug "create-comment! num:" num)
    (if (db/frozen? {:num num})
      (layout/render request "error.html"
                     {:status 403
                      :title "Py99 is Frozen"
                      :message "回答受け付けを停止してます。"})
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
  ;;(timbre/info "comments" (login request))
  (layout/render request "comments.html"
                 {:comments (drop 20 (db/comments))}))

(defn comments-by-num [request]
  (let [num (Integer/parseInt (get-in request [:path-params :num]))]
    ;;(timbre/info "comments-by-num" (login request))
    (layout/render request "comments.html"
                   {:comments (db/comments-by-num {:num num})})))

;; (defn ch-pass [{{:keys [old new]} :params :as request}]
;;   (let [login (login request)
;;         user (db/get-user {:login login})]
;;     ;;(timbre/info "ch-pass" login)
;;     (if (and (seq user) (hashers/check old (:password user)))
;;       (do
;;         (db/update-user! {:login login :password (hashers/derive new)})
;;         (redirect "/login"))
;;       (layout/render request "error.html"
;;                      {:message "did not match old password"}))))

;;
;; weekly counts
;;
(defn- before? [s1 s2]
  (< (compare s1 s2) 0))

(defn- count-up [m]
  (reduce + (map :count m)))

(defn bin-count
  "data は週ごとの集計。単純な answers や comments じゃないので、
   (count-up f)が必要。"
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
    ;;(timbre/info "profile who?" {:login login})
    (layout/render {} "profile.html"
                   {:login login
                    :user (db/get-user {:login login})
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
  ;; (timbre/info "profile-login" (login request))
  (if (admin? (login request))
    (profile (get-in request [:path-params :login]))
    (layout/render request "error.html"
                   {:status 403
                    :title "Access Forbidden"
                    :message "admin only. "})))

(defn ranking [request]
  ;; (timbre/info "ranking" (login request))
  (layout/render request "ranking.html"
                 {:submissions (take 30 (db/submissions))
                  :solved      (take 30 (db/solved))
                  :comments    (take 30 (db/comments-counts))
                  :login (login request)
                  :n 30}))

(defn rank-submissions [request]
  (let [login (login request)]
    ;; (timbre/info "rank-submissions" login)
    (layout/render request "ranking-all.html"
                   {:data (db/submissions)
                    :title "Ranking Submissions"
                    :login  login
                    :admin? (admin? login)})))

(defn rank-solved [request]
  (let [login (login request)]
    ;; (timbre/info "rank-solved" login)
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
    ;; (timbre/info "rank-comments" login)
    (layout/render request "ranking-all.html"
                   {:data data
                    :title "Comments Ranking"
                    :login  login
                    :admin? (admin? login)})))

(defn answers-by-problems [request]
  (let [data (db/answers-by-problems)]
    ;; (timbre/info "answers-by-problems" (login request))
    (layout/render request "answers-by-problems.html"
                   {:data data
                    :title "Answers by Problems"})))

(defn home-routes []
  ["" {:middleware [middleware/auth
                    middleware/wrap-csrf
                    middleware/wrap-formats]}
   ["/" {:get status-page}]
   ["/answers" {:get answers-by-problems}]
   ["/answer/:num" {:get  answer-page
                    :post create-answer!}]
   #_["/ch-pass" {:post ch-pass}]
   ["/comment/:id" {:get  comment-form
                    :post create-comment!}]
   ["/comments" {:get comments}]
   ["/comments-sent/:login" {:get comments-sent}]
   ["/comments/:num" {:get comments-by-num}]
   ["/problems" {:get problems-page}]
   ["/profile" {:get profile-self}]
   ["/profile/:login" {:get profile-login}]
   ["/ranking" {:get ranking}]
   ["/rank/submissions" {:get rank-submissions}]
   ["/rank/solved"      {:get rank-solved}]
   ["/rank/comments"    {:get rank-comments}]
   ["/wp" {:get (fn [_]
                  {:status 200
                   :headers {"Content-Type" "text/html"}
                   :body (slurp (io/resource "docs/weekly-points.html"))})}]])
