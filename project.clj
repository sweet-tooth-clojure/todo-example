(defproject sweet-tooth-todo-example "0.1.0-SNAPSHOT"
  :description "a sweet little to-do example"
  :min-lein-version "2.0.0"

  :plugins [[duct/lein-duct "0.12.1"]
            [lein-tools-deps "0.4.5"]]

  :lein-tools-deps/config {:config-files [:install :user :project]
                           :aliases      [:backend :dev :test]}

  :resource-paths ["resources" "target/resources"]
  :middleware     [lein-duct.plugin/middleware
                   lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]

  :target-path    "target/%s/"
  :main           ^:skip-aot sweet-tooth.todo-example.backend.core

  :profiles
  {;; generated by duct
   :dev          [:project/dev :profiles/dev]
   :repl         {:repl-options {:init-ns user}}
   :uberjar      {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths           ["dev/src"]
                  :resource-paths         ["dev/resources" "target/dev/resources"]
                  :target-path            "target/dev/"
                  :lein-tools-deps/config {:aliases [:dev :test]}
                  :plugins                [[test2junit "1.4.2"]]
                  :test2junit-output-dir ".out/test-results"}

   :staging {:target-path            "target/staging/"
             :resource-paths         ["dev/resources" "frontend-target/staging"]
             :lein-tools-deps/config {:aliases ^:replace [:backend]}}
   :prod    {:target-path            "target/prod/"
             :resource-paths         ["frontend-target/prod"]
             :lein-tools-deps/config {:aliases ^:replace [:backend]}}

   :test {:resource-paths         ["dev/resources" "frontend-target/test"]
          :lein-tools-deps/config {:aliases [:test]}}})
