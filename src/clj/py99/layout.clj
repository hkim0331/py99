(ns py99.layout
  (:require
   [clojure.java.io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [markdown.core :refer [md-to-html-string]]
   [py99.db.core :as db]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.http-response :refer [content-type ok]]
   [ring.util.response]
   [selmer.filters :as filters]
   [selmer.parser :as parser]))

(parser/set-resource-path!  (clojure.java.io/resource "html"))

(parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))

(filters/add-filter! :markdown (fn [content] [:safe (md-to-html-string content)]))

(filters/add-filter! :shorten-login
                     (fn [s] (str/replace s #"[_\-0123456789]+$" "")))

(defn render
  "renders the HTML template located relative to resources/html"
  [request template & [params]]
  (let [login (name (get-in request [:session :identity] :nobody))
        num (get-in params [:problem :num] 0)
        action (-> template
                   (str/replace #".html$" "")
                   (str/replace #"-form$" ""))]
    (log/info login action num)
    (db/action! {:login login
                 :num num
                 :action action})
    (content-type
     (ok
      (parser/render-file
       template
       (assoc params
              :page template
              :csrf-token *anti-forgery-token*)))
     "text/html; charset=utf-8")))

(defn error-page
  "error-details should be a map containing the following keys:
   :status - error status
   :title - error title (optional)
   :message - detailed error message (optional)

   returns a response map with the error page as the body
   and the status specified by the status key"
  [error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (parser/render-file "error.html" error-details)})
