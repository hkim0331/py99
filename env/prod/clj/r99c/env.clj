(ns py99.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[py99 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[py99 has shut down successfully]=-"))
   :middleware identity})
