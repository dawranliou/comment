(ns comment.system
  (:require
   [clojure.pprint :refer [pprint]]
   [integrant.core :as ig]
   [comment.config :as config]
   [comment.db :as db]
   [comment.handler :as handler]
   [ring.adapter.jetty :as jetty]))

(defmethod ig/init-key :db [_ db-spec]
  (db/setup-connection-pool db-spec))

(defmethod ig/halt-key! :db [_ datasource]
  (db/stop-connection-pool datasource))

(defmethod ig/init-key :app [_ config]
  (handler/create-app config))

(defmethod ig/init-key :server [_ {:keys [app port]}]
  (println "Server starting at port:" port)
  (jetty/run-jetty app {:port port :join? false}))

(defmethod ig/halt-key! :server [_ server]
  (.stop server))

(defn -main [& args]
  (ig/init config/system-config))
