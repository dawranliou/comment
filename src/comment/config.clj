(ns comment.config
  (:require
   [integrant.core :as ig]))

(defn default-config []
  {:db     {:dbtype "sqlite"
            :dbname "comment.db"}
   :app    {:db (ig/ref :db)}
   :server {:app  (ig/ref :app)
            :port 8091}})
