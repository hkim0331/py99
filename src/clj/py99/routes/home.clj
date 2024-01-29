(ns py99.routes.home
  (:require
   ;; [clj-time.core :as t]
   [clj-time.local :as l]
   ;; [clj-time.periodic :as p]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [digest]
   [jx.java.shell :refer [timeout-sh]]
   [py99.charts :refer [class-chart individual-chart comment-chart]]
   ;; clj-kondo can not trace defstate
   [py99.config :refer [env weeks period]] ;; defstate env
   [py99.db.core :as db]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   ;; [py99.routes.login :refer [get-user]]
   [ring.util.http-response :as response]
   [ring.util.response :refer [redirect]]
   [selmer.filters :refer [add-filter!]]))

;; https://stackoverflow.com/questions/16264813/
;; clojure-idiomatic-way-to-call-contains-on-a-lazy-sequence
(defn- lazy-contains? [col key]
  (some #{key} col))

(defn- to-date-str
  "FIXME: strongly depends on format of `s`."
  [s]
  (-> (str s)
      (subs 0 10)))

;; (defn- make-period
;;   "return a list of days from `yyyy-mm-dd` to days after from it."
;;   [yyyy mm dd days]
;;   (let [start-day (l/to-local-date-time (t/date-time yyyy mm dd))]
;;     (->> (take days (p/periodic-seq start-day (t/days 1)))
;;          (map to-date-str))))

;; 情報応用の授業期間。2023-10-01 から 150 日間。
;; chart の横軸になる。
;; (def ^:private period (make-period 2023 10 1 150))

(defn- today []
  (to-date-str (str (l/local-now))))

(defn- up-to-today
  "return a list of `yyyy-mm-dd ` up to today from the day class started."
  []
  (remove #(pos? (compare % (today))) period))

;; moved to py99.config
;; ;; weekly reports の〆切日
;; (def ^:private weeks
;;   ["2023-10-02" "2023-10-09" "2023-10-16" "2023-10-23" "2023-10-30"
;;    "2023-11-06" "2023-11-13" "2023-11-20" "2023-11-27"
;;    "2023-12-04" "2023-12-11" "2023-12-18" "2023-12-25"
;;    "2024-01-01" "2024-01-08" "2024-01-15" "2024-01-22" "2024-01-29"
;;    "2024-02-05" "2024-02-12" "2024-02-19"])

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
        ;; _ (println one five fifteen)
        busy (- (int (first one)) (int \0))
        busy-mark (cond
                    (<= 5 busy) "🔴"
                    (<= 1 busy) "🟡"
                    :else "🟢")]
    (str busy-mark
         " "
         (str one five fifteen)
         " (過去 1, 5, 15 分間のサーバ負荷)")))

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
        individual  (db/answers-by-date-login {:login login})
        all-answers (db/answers-by-date)
        no-thanks (get-in request [:session :filter])]
    ;; (log/debug "status-page" login)
    (layout/render
     request
     "status.html"
     {:login login
      :status (map #(solved? solved %) (map :num (db/problems)))
      :individual-chart (individual-chart individual period 600 150)
      :class-chart (class-chart all-answers period 600 150)
      :recents
      (->> (db/recent-answers {:n 20})
           (remove #(= (:login %) no-thanks)))
      :recent-comments
      (->> (db/recent-comments {:n 20})
           (remove #(= (:from_login %) no-thanks)))})))

(defn problems-page
  "display problems."
  [request]
  ;; (log/debug "problem-page" (login request))
  (layout/render request "problems.html" {:problems (db/problems)}))

;; title
(defn answer-page
  "Take problem number `num` as path parameter, prep answer to the
   problem."
  [request]
  (let [num (Integer/parseInt (get-in request [:path-params :num]))
        problem (db/get-problem {:num num})
        answers (db/answers-to {:num num})
        frozen? (db/frozen? {:num num})
        uptime (uptime)]
    ;; (log/debug "answer-page" (login request))
    ;; この if の理由？
    (if-let [answer (db/get-answer {:num num :login (login request)})]
      (let [answers (group-by #(= (:md5 answer) (:md5 %)) answers)]
        (layout/render request
                       "answer-form.html"
                       {:problem problem
                        :same (answers true)
                        :differ (answers false)
                        :frozen? frozen?
                        :uptime uptime
                        :exam? (env :exam-mode)
                        :today (today)}))
      (layout/render request
                     "answer-form.html"
                     {:problem problem
                      :same []
                      :differ answers
                      :frozen? frozen?
                      :uptime uptime
                      :exam? (env :exam-mode)}))))

;; validations
;; FIXME: remove docstring
(defn- remove-comments
  "Remove lines starting from #, they are comments in Python."
  [s]
  (apply
   str
   (interpose "\n"
              (remove #(str/starts-with? % "#") (str/split-lines s)))))

(defn remove-docstrings
  "Remove all \"\"\" ~ \"\"\" parts from `s`."
  [s]
  (-> s
      (str/replace #"\n" "")
      ;; must use shotest match. 2023-11-24
      (str/replace #"\"\"\".+?\"\"\"", "")))

(defn- strip
  "just use in not-empty-test, digest/md5."
  [s]
  (-> s
      (str/replace #"[ \t]" "")
      remove-comments
      remove-docstrings))

(defn- not-empty-test
  "called as (not-empty-test (strip answer)).
   so, no use to call `strip` inside this function. 2023-11-24."
  [answer]
  (when-not (re-find #"\S" answer)
    (throw (Exception. "answer is empty"))))

;; changed 2022-12-25, was 60
;; changed 2023-12-20, was 30, zono insisted.
(def ^:private timeout 10)

(defn black-test
  "check black results on trimmed `answer`. "
  [answer]
  (let [tempfile (java.io.File/createTempFile "python" ".py")]
    (with-open [file (io/writer tempfile)]
      (binding [*out* file]
        (println (str/trim answer))))
    (let [ret (timeout-sh timeout
                          "black"
                          "--diff"
                          "--check"
                          (.getAbsolutePath tempfile))]
      (.delete tempfile)
      ;; (println ret)
      (when-not (zero? (:exit ret))
        (throw (Exception. (str "Black complained")))))))

(defn pytest-test
  "Fetch testcode from `num`, test string `answer`.
   Throw exception when pytest on them fails."
  [num answer]
  (when-let [test (:test (db/get-problem {:num num}))]
    ;; FIXME: to skip validations,
    ;;        current py99 requires empty tests.
    (when (re-find #"\S" test)
      ;; (log/debug "test is not empty" test)
      (let [tempfile (java.io.File/createTempFile "python" ".py")]
        (with-open [file (clojure.java.io/writer tempfile)]
          (binding [*out* file]
            (println "#-*- coding: UTF-8 -*-")
            (println answer)
            (println test)))
        (let [ret (timeout-sh timeout
                              "pytest"
                              (.getAbsolutePath tempfile))]
          ;; (log/debug "ret" ret)
          (.delete tempfile)
          (when-not (zero? (:exit ret))
            (throw (Exception. (->> (str/split-lines (:out ret))
                                    (filter #(re-find #"^[>E]" %))
                                    (str/join "\n"))))))))))

(defn- get-answer
  "get user login's answer to `num` from db."
  [num login]
  ;; (log/debug "get-answer" num login)
  (if-let [ans (:answer (db/get-answer {:num num :login login}))]
    ans
    (throw (Exception. (str "P-" num " の回答が見当たりません。")))))

(comment
  (defn expand-includes
    "expand `#include` recursively."
    [s login]
    (str/join
     "\n"
     (for [line (str/split-lines s)]
       (if (str/starts-with? line "#include ")
         (let [[_ num] (str/split line #"\s+")]
           (when-not (re-matches #"\d+" num)
             (throw (Exception. "#include の後に問題番号がありません。")))
           (expand-includes (get-answer (Integer/parseInt num) login) login))
         line))))
  :rcf)

;; allow `# include nnn`
;; 2023-10-13
(defn expand-includes
  "expand `#include` recursively."
  [s login]
  (str/join
   "\n"
   (for [line (str/split-lines s)]
     (if-let [[_ num] (re-matches #"#\s*include\s*(\d+).*" line)]
       (expand-includes (get-answer (Integer/parseInt num) login) login)
       line))))

;; 2023-10-19
(defn- has-docstring-test
  "if s contains docstring returns nil or throw.
   FIXME: should check `def` proceeds the comment line."
  [lines]
  (if (some
       (fn [s] (or (re-find #"^\s+\"\"\"" s)
                   (re-find #"^\s+\s\'\'\'" s)))
       (str/split-lines lines))
    nil
    (throw (Exception. "no docstring"))))

(defn- not-same-md5-login
  "s is a stripped answer."
  [s login]
  (if (seq (db/answers-same-md5-login {:md5 (digest/md5 s)
                                       :login login}))
    (throw (Exception. "no need to send a same answer."))
    nil))

(comment
  (not-same-md5-login "abc" "hkimura")
  :rcf)

(defn- starts-with-def-import-from-indent?
  [s]
  (or (str/starts-with? s " ")
      (str/starts-with? s "#")
      (str/starts-with? s "\t")
      (str/starts-with? s "def")
      (str/starts-with? s "from")
      (str/starts-with? s "import")
      (str/starts-with? s "g_")
      ;; doctest, 2024-01-08
      (str/starts-with? s "if")))

(defn- no-exec-statements
  [s]
  (let [lines (->> (str/split-lines s)
                   (remove #(re-matches #"" %)))]
    ;; (prn "no-exec-statements" lines)
    (when-not (every? true?  (map starts-with-def-import-from-indent? lines))
      ;; (prn (map starts-with-def-import-from-indent? lines))
      (throw (Exception. "include exec statements.")))))

(comment
  (starts-with-def-import-from-indent? "def")
  (starts-with-def-import-from-indent? " ")
  (starts-with-def-import-from-indent? "print")
  :rcf)



(defn- validate
  "Return nil if all validations success, or raize exeption."
  [num answer login]
  (let [stripped (strip answer)]
    (try
      (not-empty-test stripped)
      (has-docstring-test answer)
      (no-exec-statements answer)
      (not-same-md5-login stripped login)
      (black-test answer)
      (pytest-test num (expand-includes answer login))
      nil
      (catch Exception e (throw (Exception. (.getMessage e)))))))

(defn create-answer!
  [{{:keys [num answer]} :params :as request}]
  (log/debug "create-answer!" (login request) num)
  (try
    (when-not (env :exam-mode)
      (validate (Integer/parseInt num) answer (login request)))
    (db/create-answer!
     {:login (login request)
      :num (Integer/parseInt num)
      :answer answer
      :md5 (-> answer strip digest/md5)})
    ;; 2023-10-20
    (db/action! {:login (name (login request))
                 :action "answer(!)"
                 :num (Integer/parseInt num)})
    ;; 2023-10-15
    ;; (if (env :dev)
    ;;   (redirect (str "/answer/" num))
    ;;   (redirect "https://qa.melt.kyutech.ac.jp/qs"))
    ;; resume 2023-10-24
    (redirect (str "/answer/" num))
    (catch Exception e
      (layout/render request "error.html"
                     {:status 406
                      :message "ブラウザのバックで戻って、修正後、再提出してください。"
                      :exception (.getMessage e)}))))

(defn comment-form
  "Taking answer id as path-parameter, show the answer with
   comment form."
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))
        answer (db/get-answer-by-id {:id id})
        num (:num answer)
        my-answer (db/get-answer {:num num :login (login request)})
        exam? (env :exam-mode)
        uptime (uptime)]
    (if my-answer ;; (and my-answer (not exam?))
      (layout/render request "comment-form.html"
                     {:answer   (if exam? my-answer answer)
                      :problem  (db/get-problem {:num num})
                      :same-md5 (db/answers-same-md5 {:md5 (:md5 answer)})
                      :comments (when-not exam? (db/get-comments {:a_id id}))
                      :uptime   uptime
                      :exam?    exam?})
      (layout/render request "error.html"
                     {:status 403
                      :title "Access Forbidden"
                      :message "まず自分で解いてから。"}))))


(defn create-comment! [request]
  (let [params (:params request)
        num (Integer/parseInt (:p_num params))]
    ;;(log/debug "create-comment!" (login request) num)
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
        ;; 2023-10-20
        (db/action! {:login (name (login request))
                     :action "comment(!)"
                     :num num})
        (redirect "/")
        (catch Exception _
          (layout/render request "error.html"
                         {:status 406
                          :title "frozen r99"
                          :message "can not add comments"}))))))

(defn submissions [request]
  (let [login (or (get-in request [:path-params :login])
                  (get-in request [:params :login]))
        submissions (-> (db/answer-by-login {:login login})
                        reverse)]
    (layout/render request
                   "submissions.html"
                   {:submissions submissions})))

(defn comments-sent
  "path-params と form-params の両方に対応する。"
  [request]
  (let [login (or (get-in request [:path-params :login])
                  (get-in request [:params :login]))
        sent (db/comments-sent {:login login})]
    (layout/render request "comments-sent.html" {:sent sent})))

(defn comments [request]
  (layout/render request "comments.html"
                 {:comments (drop 20 (db/comments))
                  :num "all"}))

(defn comments-by-num [request]
  (let [num (Integer/parseInt (get-in request [:path-params :num]))]
    (layout/render request "comments.html"
                   {:comments (db/comments-by-num {:num num})
                    :num num})))

;;
;; weekly counts
;;
(defn- before? [s1 s2]
  ;; 2022-10-20 s/</<=/
  (<= (compare s1 s2) 0))

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

;; CHANGED 2023-10-20, bug, resume.
(defn profile
  [request]
  (log/debug "user" (:user request))
  (let [login (get request :user (login request))
        solved (db/answers-by {:login login})
        individual (db/answers-by-date-login {:login login})
        comments (db/comments-by-date-login {:login login})]
    (layout/render request
                   "profile.html"
                   {:login login
                    ;;
                    ;; :actions actions
                    ;; :user login
                    :today (today)
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
                    :groups (filter #(< 200 (:num %)) solved)
                    :points (db/points? {:login login})})))


(defn profile-self
  [request]
  (profile request))

(defn profile-login
  "method for admin only."
  [request]
  (log/debug "profile-login" (get-in request [:path-params :login]))
  (if (admin? (login request))
    (let [user (get-in request [:path-params :login])]
      ;; :flash は用途では使えない。
      (profile (assoc request :user user)))
    (layout/render request "error.html"
                   {:status 403
                    :title "Access Forbidden"
                    :message "admin only. "})))

(defn ranking [request]
  ;; (log/debug "ranking" (login request))
  (layout/render request "ranking.html"
                 {:submissions (take 30 (db/submissions))
                  :solved      (take 30 (db/solved))
                  :comments    (take 30 (db/comments-counts))
                  :login (login request)
                  :n 30}))

(defn rank-submissions [request]
  (let [login (login request)]
    ;; (log/debug "rank-submissions" login)
    (layout/render request "ranking-all.html"
                   {:data (db/submissions)
                    :title "Ranking Submissions"
                    :login  login
                    :admin? (admin? login)})))

(defn rank-solved [request]
  (let [login (login request)]
    ;; (log/debug "rank-solved" login)
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
    ;; (log/debug "rank-comments" login)
    (layout/render request "ranking-all.html"
                   {:data data
                    :title "Comments Ranking"
                    :login  login
                    :admin? (admin? login)})))

(defn answers-by-problems [request]
  (let [data (db/answers-by-problems)]
    ;; (log/debug "answers-by-problems" (login request))
    (layout/render request "answers-by-problems.html"
                   {:data (reverse data)
                    :title "Answers by Problems"
                    :today (today)})))

(defn create-stock! [request]
  (let [login (login request)
        a_id (-> (get-in request [:params :a_id])
                 Integer/parseInt)
        note (-> (get-in request [:params :note]))]
    (log/debug "create-stock!" login a_id note)
    (try
      (db/create-stock! {:login login :a_id a_id :note note})
      (db/action! {:login login
                   :action "stock(!)"
                   :num a_id})
      (redirect (str "/comment/" a_id))
      (catch Exception e
        (layout/render nil "error.html"
                       {:status 406
                        :message "create stock error"
                        :exception (.getMessage e)})))))

(defn list-stocks [request]
  (let [login (login request)]
    ;; (log/debug "list-stocks" login)
    (layout/render request "stocks.html"
                   {:stocks (db/stocks? {:login login})})))

(defn list-todays
  [{{:keys [date]} :path-params :as request}]
  ;; (log/debug "list-todays" date)
  (if (re-matches #"\d\d\d\d-\d\d-\d\d" date)
    (layout/render request "todays.html"
                   {:date date
                    :todays (db/todays? {:date date})})
    (layout/render request "error.html"
                   {:status 403
                    :title "date format error"
                    :message "日付のフォーマットになってない。"})))

(defn list-todays-today [request]
  (layout/render request "todays.html"
                 {:date (today)
                  :todays (db/todays? {:date today})}))


(defn midterm [request]
  (layout/render request "midterm.html"))

(defn comments-count [request]
  (layout/render request
                 "comments-count.html"
                 {:login (login request)
                  :comments (db/comments-count-by-number)}))
;; 2023-12-10
(defn s-point-days
  [{{:keys [login]} :path-params}]
  (log/info "s-point-days" login)
  (let [date-count (db/answers-by-date-login {:login login})
        dc (apply merge (for [mm date-count]
                          {(:create_at mm) (:count mm)}))]
    (log/info "dc" dc)
    (response/ok (map #(get dc % 0) (up-to-today)))))

(defn s-point
  [request]
  (log/info "s-point" (login request))
  (s-point-days {:path-params {:login (login request)}}))

(defn activities-page
  [request]
  (let [login (login request)
        today (today)
        activities (db/actions? {:login login :date today})]
    (layout/render request
                   "user-actions-page.html"
                   {:login login
                    :date today
                    :actions activities})))

;; FIXME: (assoc-in [:session :identity])が必要な理由は？
(defn add-filter
  [{{:keys [filter]} :params :as request}]
  (println "session:" (:session request))
  (-> (response/found "/")
      (assoc-in [:session :identity] (get-in request [:session :identity]))
      (assoc-in [:session :filter] filter)))


(defn home-routes []
  ["" {:middleware [middleware/auth
                    middleware/wrap-csrf
                    middleware/wrap-formats]}
   ["/" {:get status-page}]
   ["/activities" {:get activities-page}]
   ["/answers" {:get answers-by-problems}]
   ["/answer/:num" {:get  answer-page
                    :post create-answer!}]
   ["/comment/:id" {:get  comment-form
                    :post create-comment!}]
   ["/comments" {:get comments}]
   ["/comments-sent" {:get comments-sent}]
   ["/comments-sent/:login" {:get comments-sent}]
   ["/comments-count" {:get comments-count}]
   ["/comments/:num" {:get comments-by-num}]
   ["/filter" {:get add-filter}]
   ["/midterm" {:get midterm}]
   ["/problems" {:get problems-page}]
   ["/profile" {:get profile-self}]
   ["/profile/:login" {:get profile-login}]
   ["/ranking" {:get ranking}]
   ["/rank/submissions" {:get rank-submissions}]
   ["/rank/solved"      {:get rank-solved}]
   ["/rank/comments"    {:get rank-comments}]
   ["/s-point" {:get s-point}]
   ["/s-point/:login" {:get s-point-days}]
   ["/stock" {:post create-stock!
              :get  list-stocks}]
   ["/submissions" {:get submissions}]
   ["/todays" {:get list-todays-today}]
   ["/todays/:date" {:get list-todays}]
   ["/wp" {:get (fn [_]
                  {:status 200
                   :headers {"Content-Type" "text/html"}
                   :body (slurp (io/resource "docs/weekly-points.html"))})}]])
