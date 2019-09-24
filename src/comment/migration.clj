(ns comment.migration
  (:require
   [migratus.core :as migratus]
   [comment.config :as config]))

(def config (merge (select-keys (config/default-config) [:db])
                   {:store         :database
                    :migration-dir "migrations"}))

(defn migrate []
  (migratus/migrate config))

(defn rollback []
  (migratus/rollback config))

(defn create [migration-name]
  (migratus/create migration-name))

(defn -main [action & [arg]]
  (case action
    "migrate"  (migrate)
    "rollback" (rollback)
    "create"   (create arg)
    (println "Unknown action:" action ", arg: " arg)))
