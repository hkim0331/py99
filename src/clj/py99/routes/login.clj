(ns py99.routes.login
  (:require
   [buddy.hashers :as hashers]
   [clojure.tools.logging :as log]
   [hato.client :as hc]
   [py99.layout :as layout]
   [py99.middleware :as middleware]
   [ring.util.response :refer [redirect]]
   [struct.core :as st]
   #_[py99.db.core :as db]))

(def ^:private version "0.47.2")

(def ^:private l22 "https://l22.melt.kyutech.ac.jp")

(defn get-user
  "retrieve str login's info from API.
   note: parameter is a string. cf. (db/get-user {:login login})"
  [login]
  (let [ep (str l22 "/api/user/" login)
        resp (hc/get ep {:as :json})]
    (log/info "login" (get-in resp [:body :login]))
    ;; (log/debug "(:body resp)" (:body resp))
    (:body resp)))

(def users-schema
  [[:sid
    st/required
    st/string
    {:message "学生番号は数字3つに英大文字、続いて数字4つです。"
     :validate (fn [sid] (re-matches #"^\d{3}[A-Z]\d{4}" sid))}]
   [:name
    st/required
    st/string]
   [:login
    st/required
    st/string
    {:message "同じユーザ名があります。"
     :validate (fn [login]
                 (let [ret (get-user login)]
                   (log/debug "validate ret:" ret)
                   (empty? ret)))}]
   [:password
    st/required
    st/string]])

(defn validate-user [params]
  (let [ret (st/validate params users-schema)]
    (log/debug "validate:" ret)
    (first ret)))

(defn about-page [request]
  ;;(log/info "about-page" (login request))
  (layout/render request "about.html" {:version version}))

(defn admin-only [request]
  (layout/render request "error.html" {:status 401
                                       :title "Unauthorized"
                                       :message "This page is admin only."}))

(defn login [request]
  (layout/render request "login.html" {:flash (:flash request)}))

(defn login-post [{{:keys [login password]} :params}]
  (let [user (get-user login)]
    (if (and (seq user)
           (= (:login user) login)
           (hashers/check password (:password user)))
      (do
        (log/info "login success" login)
        ;; in read-only mode, can not this.
        ;; (db/login {:login login})
        (-> (redirect "/")
            (assoc-in [:session :identity] (keyword login))))
      (do
        (log/info "login faild" login)
        (-> (redirect "/login")
            (assoc :flash "login failure"))))))

(defn logout [_]
  (-> (redirect "/")
      (assoc :session {})))

;; (defn register [{:keys [flash] :as request}]
;;   (layout/render request
;;                  "register.html"
;;                  {:errors (select-keys flash [:errors])}))

;; (defn register-post [{params :params}]
;;   (if-let [errors (validate-user params)]
;;     (-> (redirect "/register")
;;         (assoc :flash (assoc params :errors errors)))
;;     (try
;;       (db/create-user! (assoc (dissoc params :password)
;;                               :password (hashers/derive (:password params))))
;;       (redirect "/login")
;;       (catch Exception _
;;         (redirect "/register")))))

(defn login-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/about" {:get about-page}]
   ["/admin-only" {:get admin-only}]
   ["/login" {:get  login
              :post login-post}]
   ["/logout" {:get logout}]
   #_["/register" {:get  register
                   :post register-post}]])

