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
  (jetty/run-jetty app {:port port :join? false}))

(defmethod ig/init-key :server [_ {:keys [app port]}]
  (jetty/run-jetty app {:port port :join? false}))

(defmethod ig/halt-key! :server [_ server]
  (.stop server))

(defn mask [s]
  "[secret]")

(defn start-system!
  ([]
   (start-system! config/system-config))
  ([config]
   (let [system (ig/init config)]
     (prn "System started with config:")
     (pprint (-> config
                 (update-in [:db :password] mask)))
     system)))

(defn stop-system! [system]
  (ig/halt! system))

(defn -main [& args]
  (start-system! config/system-config))
