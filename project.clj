(defproject py99 "0.61.0"

  :description "r99 revised with clojure"
  :url "https://r99.melt.kyutech.ac.jp"

  :dependencies
  [[ch.qos.logback/logback-classic "1.4.5"]
   [clojure.java-time "1.2.0"]
   [conman "0.9.6"]
   [cprop "0.1.19"]
   [expound "0.9.0"]
   [funcool/struct "1.4.0"]
   [json-html "0.4.7"]
   [luminus-migrations "0.7.5"]
   [luminus-transit "0.1.5"]
   [luminus-undertow "0.1.17"]
   [luminus/ring-ttl-session "0.3.3"]
   [markdown-clj "1.11.4"]
   [metosin/muuntaja "0.6.8"]
   [metosin/reitit "0.5.18"]
   [metosin/ring-http-response "0.9.3"]
   [mount "0.1.17"]
   [nrepl "1.0.0"]
   [org.clojure/clojure "1.11.1"]
   [org.clojure/tools.cli "1.0.214"]
   [org.clojure/tools.logging "1.2.4"]
   [org.postgresql/postgresql "42.5.1"]
   [org.webjars.npm/bulma "0.9.4"]
   [org.webjars.npm/material-icons "1.10.8"]
   [org.webjars/webjars-locator "0.46"]
   [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
   [ring-webjars "0.2.0"]
   [ring/ring-core "1.9.6"]
   [ring/ring-defaults "0.3.4"]
   [selmer "1.12.55"]
   ;;
   [buddy/buddy-auth "3.0.323"]
   [buddy/buddy-hashers "1.8.158"]
   [cheshire/cheshire "5.11.0"]
   [clj-time/clj-time "0.15.2"]
   [digest "1.4.10"]
   [environ/environ "1.2.0"]
   [hato/hato "0.9.0"]
   [hiccup "1.0.5"]
   [timeout-shell "1.0.0"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot py99.core

  :plugins []

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "py99.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[org.clojure/tools.namespace "1.3.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.9.6"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.25.0"]
                                 [jonase/eastwood "1.3.0"]
                                 [cider/cider-nrepl "0.29.0"]]

                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
