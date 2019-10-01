(ns comment.system
  (:require
   [integrant.core :as ig]
   [ring.adapter.jetty :as jetty]
   [comment.handler :as handler]))

(defn system-config []
  {:comment/db     {}
   :comment/app    {:db (ig/ref :comment/db)}
   :comment/server {:app  (ig/ref :comment/app)
                    :port 8000}})

(defmethod ig/init-key :comment/db [_ _]
  {})

(defmethod ig/init-key :comment/app [_ {:keys [db]}]
  (handler/create-app db))

(defmethod ig/init-key :comment/server [_ {:keys [app port]}]
  (println "server running in port" port)
  (jetty/run-jetty app {:port port :join? false}))

(defmethod ig/halt-key! :comment/server [_ server]
  (.stop server))

(defn -main [& args]
  (ig/init (system-config)))

(comment
  ;; start system
  (def system (ig/init system-config))
  ;; stop system
  (ig/halt! system))
