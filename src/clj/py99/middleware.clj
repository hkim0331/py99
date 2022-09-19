(ns py99.middleware
  (:require
   [clojure.tools.logging :as log]
   [muuntaja.middleware :refer [wrap-format wrap-params]]
   ;;[py99.config :refer [env]]
   [py99.env :refer [defaults]]
   [py99.layout :refer [error-page]]
   [py99.middleware.formats :as formats]
   [ring.adapter.undertow.middleware.session :refer [wrap-session]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.flash :refer [wrap-flash]]
   ;;
   [buddy.auth :refer [authenticated? #_throw-unauthorized]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends.session :refer [session-backend]]
   [buddy.auth.middleware :refer [wrap-authorization wrap-authentication]]
   [py99.db.core :as db]
   [ring.util.response :refer [redirect]]))

(defn unauthorized-handler
  [request _]
  (if (authenticated? request)
    ;;(throw-unauthorized)
    (redirect "/admin-only")
    (redirect "/login")))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))

(defn auth [handler]
  (-> handler
      (restrict {:handler authenticated?})
      (wrap-authorization  auth-backend)
      (wrap-authentication auth-backend)))

;; (defn admin? [request]
;;    (if-let [login (get-in request [:session :identity] nil)]
;;      (boolean (:is_admin (db/get-user {:login (name login)})))
;;      false))

(defn admin? [request]
 (= :hkimura (get-in request [:session :identity] nil)))

;; Added 2021-10-06
(defn admin [handler]
  (-> handler
      (restrict {:handler admin?})
      ;;(restrict {:handler authenticated?})
      ;; necessary following lines?
      (wrap-authorization  auth-backend)
      (wrap-authentication auth-backend)))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
   handler
   {:error-response
    (error-page
     {:status 403
      :title "Invalid anti-forgery token"})}))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true}})
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
           (dissoc :session)))
      wrap-internal-error))
