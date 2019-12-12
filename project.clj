(defproject overtone-midi "0.1.0-SNAPSHOT"
  :description "Extends midi-file uses on livecoding with Overtone"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[overtone/overtone "0.10.3"]
                 [org.clojure/tools.namespace "0.3.1"]]
  :native-path "nativde"
  :profile 
    {:repl 
      {:repl-options
        {:init-ns user
         :host "127.0.0.1"
         :port 47480}}}
  :source-paths ["src"])

