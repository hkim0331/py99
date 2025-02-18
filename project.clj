(defproject py99 "v2.27.1296"
  :description "py99 site"
  :url "https://py99.melt.kyutech.ac.jp"
  :dependencies
  [[ch.qos.logback/logback-classic "1.5.12"]
   [clojure.java-time "1.4.3"]
   [conman "0.9.6"]
   [cprop "0.1.20"]
   [expound "0.9.0"]
   [funcool/struct "1.4.0"]
   [json-html "0.4.7"]
   [luminus-migrations "0.7.5"]
   [luminus-transit "0.1.6"]
   [luminus-undertow "0.1.18"]
   [luminus/ring-ttl-session "0.3.3"]
   [markdown-clj "1.12.2"]
   [metosin/muuntaja "0.6.10"]
   [metosin/reitit "0.7.2"]
   [metosin/ring-http-response "0.9.4"]
   [mount "0.1.20"]
   [nrepl "1.3.0"]
   [org.clojure/clojure "1.12.0"]
   [org.clojure/tools.cli "1.1.230"]
   [org.clojure/tools.logging "1.3.0"]
   [org.postgresql/postgresql "42.7.4"]
   [org.webjars.npm/bulma "1.0.2"]
   [org.webjars.npm/material-icons "1.13.2"]
   [org.webjars/webjars-locator "0.52"]
   [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
   [ring-webjars "0.3.0"]
   [ring/ring-core "1.13.0"]
   [ring/ring-defaults "0.5.0"]
   [selmer "1.12.61"]
   ;;
   [buddy/buddy-auth "3.0.323"]
   [buddy/buddy-hashers "2.0.167"]
   [cheshire/cheshire "5.13.0"]
   [clj-time/clj-time "0.15.2"]
   [digest "1.4.10"]
   [environ/environ "1.2.0"]
   [hato/hato "1.0.0"]
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
  {:uberjar
   {:omit-source true
    :aot :all
    :uberjar-name "py99.jar"
    :source-paths ["env/prod/clj"]
    :resource-paths ["env/prod/resources"]}
   :dev  [:project/dev :profiles/dev]
   :test [:project/dev :project/test :profiles/test]

   :project/dev
   {:jvm-opts ["-Dconf=dev-config.edn"]
    :dependencies [[org.clojure/tools.namespace "1.5.0"]
                   [pjstadig/humane-test-output "0.11.0"]
                   [prone "2021-04-23"]
                   [ring/ring-devel "1.13.0"]
                   [ring/ring-mock "0.4.0"]]
    :plugins      [[com.jakemccrary/lein-test-refresh "0.25.0"]
                   [jonase/eastwood "1.4.3"]
                   [cider/cider-nrepl "0.50.2"]]
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
