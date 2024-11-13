(ns py99.routes.login
  (:require
   [buddy.hashers :as hashers]
   [clojure.java.shell :refer [sh]]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [hato.client :as hc]
   [py99.layout :as layout]
   [py99.config :refer [env]]
   [py99.middleware :as middleware]
   [ring.util.response :refer [redirect]]
   #_[struct.core :as st]
   #_[py99.db.core :as db]))

(def ^:private version "v1.9.1060")
(def ^:private updated "2024-11-12 22:03:57")

(def ^:private l22 "https://l22.melt.kyutech.ac.jp")

(defn get-user
  "retrieve str login's info from API.
   note: parameter is a string. cf. (db/get-user {:login login})"
  [login]
  (let [ep (str l22 "/api/user/" login)
        resp (hc/get ep {:as :json})]
    ;; (log/debug "login" (get-in resp [:body :login]))
    (:body resp)))

(defn about-page [request]
  (layout/render request "about.html" {:version version
                                       :updated updated}))

(defn admin-only [request]
  (layout/render request "error.html" {:status 401
                                       :title "Unauthorized"
                                       :message "This page is admin only."}))

(defn login [request]
  ;; (println (:session request))
  (layout/render request "login.html" {:flash (:flash request)}))

;; TODO: filter?
(defn- filter-date [s]
  (str/split-lines s))

(defn- get-logins [login]
  (let [search (str "login success " login)
        resp (sh "grep" "-r" search "log")]
    (if (zero? (:exit resp))
      (filter-date (:out resp))
      ["error"])))

(comment
  (get-logins "hkimura")
  (sh "grep" "login success" "log")
  :rcf)

;; おバカな連中には全く効果なし。やるだけ無駄。
(defn show-logins [request]
  (let [login (name (get-in request [:session :identity]))]
    (layout/render request
                   "logins.html"
                   {:login login
                    :logins (get-logins login)})))

(defn re-exam-end [request]
  (let [login (name (get-in request [:session :identity]))]
    (layout/render request
                   "re-exam-end.html"
                   {:login login
                    :logins (get-logins login)})))
;; 2024-11-12
(defn- remote-addr [req]
  (or
   (get-in req [:headers "cf-connecting-ip"])
   (get-in req [:headers "x-real-ip"])
   (get req :remote-addr)))


  (defn login-post [{{:keys [login password #_filter]} :params :as request}]
  ;; When dev mode, it is convenient to login as administrator
  ;; without authentication.
    (if (env :dev)
      (do
        (log/debug "debug mode")
        (-> (redirect "/")
            (assoc-in [:session :identity] :hkimura)))
      (let [user (get-user login)]
        (if (and (seq user)
                 (= (:login user) login)
                 (hashers/check password (:password user)))
          (do
            (log/info "login success" login "from" (remote-addr request))
          ;; (log/info "filter:" filter)
          ;; in read-only mode, can not this.
          ;; (db/login {:login login})
          ;; after re-exam, use "/re-exam-end" instead of "/"
            (-> (redirect "/")
                (assoc-in [:session :identity] (keyword login))
                #_(assoc-in [:session :filter] filter)))
          (do
            (log/info "login faild" login)
            (-> (redirect "/login")
                (assoc :flash "login failure")))))))

  (defn logout [request]
    (log/info "logout" (get-in request [:session :identity]))
    (-> (redirect "/")
        (assoc :session {})))

;; this facility went to l22.melt
;;
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
     ["/logins" {:get show-logins}]
   ;;
     ["/re-exam-end" {:get re-exam-end}]
     #_["/register" {:get  register
                     :post register-post}]])
