(ns comment.config
  (:require
   [integrant.core :as ig]
   [aero.core :as aero]
   [clojure.java.io :as io]))

(defn system-config []
  {:db     {:dbtype "sqlite"
            :dbname "comment.db"}
   :app    {:db (ig/ref :db)}
   :server {:app  ( ig/ref :app)
            :port 8000}})

(defn secrets []
  (aero/read-config (io/resource "secrets.edn")))

(comment
  (config))
