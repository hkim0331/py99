(ns py99.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [py99.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[py99 started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[py99 has shut down successfully]=-"))
   :middleware wrap-dev})
