(defproject py99 "0.30.0-SNAPSHOT"

  :description "r99 revised with clojure"
  :url "https://r99.melt.kyutech.ac.jp"

  :dependencies
  [[ch.qos.logback/logback-classic "1.2.11"]
   [clojure.java-time "0.3.3"]
   [conman "0.9.3"]
   [cprop "0.1.19"]
   [expound "0.9.0"]
   [funcool/struct "1.4.0"]
   [json-html "0.4.7"]
   [luminus-migrations "0.7.1"]
   [luminus-transit "0.1.4"]
   [luminus-undertow "0.1.14"]
   [luminus/ring-ttl-session "0.3.3"]
   [markdown-clj "1.10.9"]
   [metosin/muuntaja "0.6.8"]
   [metosin/reitit "0.5.17"]
   [metosin/ring-http-response "0.9.3"]
   [mount "0.1.16"]
   [nrepl "0.9.0"]
   [org.clojure/clojure "1.10.3"]
   [org.clojure/tools.cli "1.0.206"]
   [org.clojure/tools.logging "1.2.4"]
   [org.postgresql/postgresql "42.3.3"]
   [org.webjars.npm/bulma "0.9.3"]
   [org.webjars.npm/material-icons "1.7.1"]
   [org.webjars/webjars-locator "0.45"]
   [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
   [ring-webjars "0.2.0"]
   [ring/ring-core "1.9.5"]
   [ring/ring-defaults "0.3.3"]
   [selmer "1.12.50"]
   ;;
   [buddy/buddy-auth "3.0.323"]
   [buddy/buddy-hashers "1.8.158"]
   [clj-time/clj-time "0.15.2"]
   [com.taoensso/timbre "5.1.2"]
   [digest "1.4.10"]
   [environ/environ "1.2.0"]
   [hato/hato "0.8.2"]
   [hiccup "1.0.5"]
   [org.clojars.hozumi/clj-commons-exec "1.2.0"]]

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
                  :dependencies [[org.clojure/tools.namespace "1.2.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.9.5"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "0.3.5"]
                                 [cider/cider-nrepl "0.28.1"]]

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
